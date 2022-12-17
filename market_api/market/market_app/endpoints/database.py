from django.shortcuts import render
from django.contrib.auth.models import User
from ..models import Profile,Product,Category,Message,Review
from django.http import JsonResponse
from ..forms.profile_forms import Register
from django.contrib.auth import login as login_user
from django.contrib.auth import authenticate, logout

def clear(request):
    User.objects.all().delete()
    Profile.objects.all().delete()
    Product.objects.all().delete()
    Category.objects.all().delete()
    Message.objects.all().delete()
    Review.objects.all().delete()            
    return JsonResponse({'status':200})

def login(request):
    if request.method == "POST":
        user = authenticate(request, username=request.POST['username'], password=request.POST['password'])
        if user is not None:
            login_user(request, user)
            return JsonResponse({'status': 200})
        else:
            return JsonResponse({'status': 404})
    return JsonResponse({'status': 400})