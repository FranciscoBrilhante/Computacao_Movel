from django.shortcuts import render
from django.contrib.auth.models import User
from ..models import Profile,Product,Category,Message,Review
from django.http import JsonResponse
from ..forms.profile_forms import Register

def clear(request):
    User.objects.all().delete()
    Profile.objects.all().delete()
    Product.objects.all().delete()
    Category.objects.all().delete()
    Message.objects.all().delete()
    Review.objects.all().delete()            
    return JsonResponse({'status':200})