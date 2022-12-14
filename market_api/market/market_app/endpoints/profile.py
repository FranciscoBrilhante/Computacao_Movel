from django.shortcuts import render
from django.contrib.auth.models import User
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth import login as login_user
from ..models import Profile
from django.http import JsonResponse,HttpResponse
from ..forms.profile_forms import Register, Login, Location, Photo
from PIL import Image

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
                return JsonResponse({'status': 200})
            else:
                return JsonResponse({'status': 404})
        print(form.errors.as_data())
    return JsonResponse({'status': 400})


def setLocation(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401})

    if request.method == 'POST':
        form = Location(request.POST)
        if form.is_valid():
            data = form.cleaned_data
            user = request.user
            user.cityX = data['cityX']
            user.cityY = data['cityY']
            user.save()
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
            profile.photo=request.FILES['profile_photo']
            profile.save()
            return JsonResponse({'status': 200})
        

    return JsonResponse({'status': 400})


def personalInfo(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401})

    user=request.user
    profile=Profile.objects.get(user=user.pk)
    username=user.username
    email=user.email
    cityX=profile.cityX
    cityY=profile.cityY
    return JsonResponse({'status': 200, 'username':username, email:email, cityX:cityX, cityY:cityY})

def profilePhoto(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401})

    user=request.user
    profile=Profile.objects.get(user=user.pk)
    photo=profile.photo
    image=Image.open(photo.path)
    
    with open(photo.path, "rb") as f:
        return HttpResponse(f.read(), content_type="image/jpeg")
#_________________ Helpers ___________________#


def delete(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401})

    

    user=request.user
    profile=Profile.objects.get(user=user.pk)
    
    logout(request)
    user.delete()
    profile.delete()

    return JsonResponse({'status': 200})



def contains_error(errors, code):
    for key, values in errors.items():
        for value in values:
            if(value.code == code):
                return True
    return False


def get_first_error_code(errors):
    for key, values in errors.items():
        for value in values:
            return value.code
    return None
