from ..models import *
from django.http import JsonResponse
from ..forms.review_forms import *
import os
from django.utils import timezone
from ..utils import *
from django.core.paginator import Paginator
from PIL import Image
from django.core.files import File
from django.conf import settings

def add(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401}) 
    if request.method == 'POST':
        form = Add(request.POST)
        if form.is_valid():
            data = form.cleaned_data
            stars=data['stars']
            userBeingReviewd=Profile.objects.get(pk=data['profile_id'])
            userReviewing=Profile.objects.get(user=request.user.pk)

            querySet=Review.objects.filter(userReviewed=userBeingReviewd).filter(userReviewer=userReviewing)
            if(querySet.exists()):
                review=querySet.first()
                review.stars=stars
                review.save()
                return JsonResponse({'status': 200})

            review=Review(stars=stars,userReviewed=userBeingReviewd,userReviewer=userReviewing,dateReviewed=timezone.now())
            review.save()
            return JsonResponse({'status': 200})

        elif contains_error(form.errors.as_data(), 'resource not found'):
                return JsonResponse({'status': 404})
        elif contains_error(form.errors.as_data(), 'invalid'):
                return JsonResponse({'status': 400})

    return JsonResponse({'status': 400})
    
        
def score(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401}) 
    if request.method == 'GET':
        form = Score(request.GET)
        if form.is_valid():
            data = form.cleaned_data

            userWithScore=Profile.objects.get(pk=data['profile_id'])

            reviews=Review.objects.filter(userReviewed=userWithScore)
            
            scores=[review.stars for review in reviews]
            if len(scores)==0:
                score=0
            else:
                score=sum(scores)/len(scores)

            return JsonResponse({'status': 200, 'score':score})

        elif contains_error(form.errors.as_data(), 'resource not found'):
                return JsonResponse({'status': 404})

    return JsonResponse({'status': 400})

def privateScore(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401}) 
    if request.method == 'GET':
        userWithScore=Profile.objects.get(user=request.user.pk)

        reviews=Review.objects.filter(userReviewed=userWithScore)
        json_data=[{'profile_id':review.userReviewer.pk, 'stars':review.stars,'date':review.dateReviewed} for review in reviews]
        
        return JsonResponse({'status': 200, 'reviews':json_data})

    return JsonResponse({'status': 400})
