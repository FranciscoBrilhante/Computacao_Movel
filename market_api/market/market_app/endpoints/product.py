from ..models import *
from django.http import JsonResponse
from ..forms.product_forms import *
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
            title=data['title']
            description=data['description']
            price=data['price']
            category=Category.objects.get(pk=data['category'])
            userSelling=Profile.objects.get(user=request.user.pk)

            product=Product(name=title,description=description,userSelling=userSelling,price=price,category=category,dateCreated=timezone.now())
            product.save()
            return JsonResponse({'status': 200,'id':product.pk})
            
        elif contains_error(form.errors.as_data(), 'resource not found'):
                return JsonResponse({'status': 404})

    return JsonResponse({'status': 400})

def addPhoto(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401})
    
    if request.method == 'POST':
        form = Photo(request.POST,request.FILES)
        if form.is_valid():
            product_id=form.cleaned_data['product_id']
            photo=form.cleaned_data['photo']
            

            if len(ProductImage.objects.filter(product=product_id))>=5:
                return JsonResponse({'status': 409})
                
            img=Image.open(photo)
            img = img.convert('RGB')
            img.thumbnail((1000, 1000))

            path=os.path.join(settings.MEDIA_ROOT,"product_pics",str(product_id)+'.jpg')
            os.makedirs(os.path.join(settings.MEDIA_ROOT,"product_pics"), exist_ok=True)
            img.save(path)
            with open(path,mode='rb') as f:
                product_img=ProductImage(product=Product.objects.get(pk=product_id),image=File(f, name=str(product_id)+'.jpg'))
                product_img.save()
            return JsonResponse({'status': 200})
        
    return JsonResponse({'status': 400})

def delete(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401}) 
    
    if request.method == 'GET':
        form = ProductId(request.GET)
        if form.is_valid():
            data = form.cleaned_data
            product_id=data['product_id']
            product = Product.objects.get(pk=product_id)
            product.delete()
            return JsonResponse({'status': 200})
            
        elif contains_error(form.errors.as_data(), 'resource not found'):
                return JsonResponse({'status': 404})

    return JsonResponse({'status': 400})


def details(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401}) 
    
    if request.method == 'GET':
        form = ProductId(request.GET)
        if form.is_valid():
            data = form.cleaned_data
            product_id=data['product_id']
            product = Product.objects.get(pk=product_id)
            
            productImages=ProductImage.objects.filter(product=product_id)
            imgs_urls=[productImage.image.url for productImage in productImages]

            return JsonResponse({'status': 200,
            'id':product.pk, 
            'title':product.name, 
            'description':product.description,
            'category':product.category.pk,
            'price':product.price,
            'profile':product.userSelling.pk,
            'date':product.dateCreated,
            'images':imgs_urls,
            'category_name':product.category.name,
            'profile_name':product.userSelling.user.username,
            'rating': computeRating(product.userSelling),
            })
            
        elif contains_error(form.errors.as_data(), 'resource not found'):
                return JsonResponse({'status': 404})

    return JsonResponse({'status': 400})

def filter(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401}) 

def recommended(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401}) 

    
    p = Paginator(Product.objects.order_by('name'), 10)
    page1 = p.page(1).object_list

    json_data=[{
        'id':product.pk, 
        'title':product.name, 
        'description':product.description,
        'category':product.category.pk,
        'price':product.price,
        'profile':product.userSelling.pk,
        'date':product.dateCreated,
        'category_name':product.category.name,
        'profile_name':product.userSelling.user.username,
        'images':[productImage.image.url for productImage in ProductImage.objects.filter(product=product.pk)],
        'rating': computeRating(product.userSelling),
    } for product in page1]

    return JsonResponse({'status': 200, 'products':json_data})

def computeRating(profile):
    reviews=Review.objects.filter(userReviewed=profile)  
    scores=[review.stars for review in reviews]
    if len(scores)==0:
        score=0
    else:
        score=sum(scores)/len(scores)

    return score