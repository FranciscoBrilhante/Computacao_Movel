from django.contrib.auth.models import User
from django.contrib.auth import authenticate, logout
from django.contrib.auth import login as login_user
from ..models import Profile
from django.http import JsonResponse
from ..forms.profile_forms import Register, Login, Location, Photo, ProfileId
from PIL import Image
from django.core.files import File
import os
from django.conf import settings
from ..utils import *
from geopy.geocoders import Nominatim

def register(request):
    if request.method == 'POST':
        form = Register(request.POST)
        if form.is_valid():
            data = form.cleaned_data

            user = User.objects.create_user(
                username=data['username'], email=data['email'], password=data['password'])
            profile = Profile(user=user, cityX=0, cityY=0)

            user.save()
            profile.save()
        
            return JsonResponse({'status': 200})
        elif contains_error(form.errors.as_data(), 'conflict'):
            return JsonResponse({'status': 409})
    return JsonResponse({'status': 400})


def login(request):
    if request.method == "POST":
        form = Login(request.POST)
        if form.is_valid():
            data = form.cleaned_data
            user = authenticate(
                request, username=data['username'], password=data['password'])
            if user is not None:
                login_user(request, user)
                profileID=Profile.objects.get(user=user.pk).pk
                return JsonResponse({'status': 200,'profile_id':profileID})
            else:
                return JsonResponse({'status': 404})
    return JsonResponse({'status': 400})


def setLocation(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401})

    if request.method == 'POST':
        form = Location(request.POST)
        if form.is_valid():
            data = form.cleaned_data
            user = request.user
            profile=Profile.objects.get(user=user.pk)
            profile.cityX = data['cityX']
            profile.cityY = data['cityY']
            profile.save()
            return JsonResponse({'status': 200})

    return JsonResponse({'status': 400})


def setPhoto(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401})

    if request.method == 'POST':
        form = Photo(request.POST,request.FILES)
        if form.is_valid():
            user=request.user
            profile=Profile.objects.get(user=user.pk)
            photo=form.cleaned_data['profile_photo']
            
            try:
                try_delete_file(profile.photo.path)
            except:
                pass
            img=Image.open(photo)
            img = img.convert('RGB')
            img.thumbnail((500, 500))
            path=os.path.join(settings.MEDIA_ROOT,"profile_pics",str(profile.pk)+'.jpg')
            os.makedirs(os.path.join(settings.MEDIA_ROOT,"profile_pics"), exist_ok=True)
            img.save(path)
            with open(path,mode='rb') as f:
                profile.photo = File(f, name=str(profile.pk)+'.jpg')
                profile.save()
            try_delete_file(path)
            return JsonResponse({'status': 200})

    return JsonResponse({'status': 400})


def personalInfo(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401})

    user=request.user
    profile=Profile.objects.get(user=user.pk)
    username=user.username
    email=user.email
    location=translateLocation(profile.cityX,profile.cityY)

    photo=profile.photo
    if photo and hasattr(photo, 'url'):
        url=photo.url
    else:
        url=None
    return JsonResponse({'status': 200, 'username':username,'id': profile.id, 'email':email, 'location':location ,'photo':url})

def delete(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401})

    user=request.user
    profile=Profile.objects.get(user=user.pk)
    
    logout(request)
    user.delete()
    profile.delete()

    return JsonResponse({'status': 200})

def info(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401}) 
    
    if request.method == 'GET':
        form = ProfileId(request.GET)
        if form.is_valid():
            data = form.cleaned_data
            profile_id=data['profile_id']
            profile = Profile.objects.get(pk=profile_id)
            try:
                photo_url=profile.photo.url
            except:
                photo_url=None

            return JsonResponse({'status': 200,
            'id':profile.pk, 
            'username':profile.user.username, 
            'location':translateLocation(profile.cityX,profile.cityY),
            'image':photo_url,
            })
            
        elif contains_error(form.errors.as_data(), 'resource not found'):
                return JsonResponse({'status': 404})

    return JsonResponse({'status': 400})