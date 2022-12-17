import os
from django.utils import timezone
from django.http import JsonResponse

from ..models import *
from ..forms.category_forms import *
from ..utils import *


def add(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401}) 

    if not request.user.is_staff:
        return JsonResponse({'status': 401}) 
    
    if request.method == 'POST':
        form = Add(request.POST)
        if form.is_valid():
            data = form.cleaned_data
            name=data['name']
        
            category=Category(name=name)
            category.save()
            return JsonResponse({'status': 200})
            
        elif contains_error(form.errors.as_data(), 'conflict'):
                return JsonResponse({'status': 409})

    return JsonResponse({'status': 400})

def delete(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401}) 

    if not request.user.is_staff:
        return JsonResponse({'status': 401}) 
    
    if request.method == 'GET':
        form = Delete(request.GET)
        if form.is_valid():
            data = form.cleaned_data
            print(data)
            categoy_id=data['category_id']
        
            category=Category(pk=categoy_id)
            category.delete()
            return JsonResponse({'status': 200})
            
        elif contains_error(form.errors.as_data(), 'resource does not exist'):
                return JsonResponse({'status': 401})

    return JsonResponse({'status': 400})

def getAll(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401}) 

    categories=Category.objects.all()
    categories_json=[{'id':category.pk,'name':category.name} for category in categories]
    return JsonResponse({'status': 200, 'categories':categories_json})
