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
            'category_name_pt':product.category.namePT,
            'profile_location':translateLocation(product.userSelling.cityX,product.userSelling.cityY),
            'profile_name':product.userSelling.user.username,
            'rating': computeRating(product.userSelling),
            })
            
        elif contains_error(form.errors.as_data(), 'resource not found'):
                return JsonResponse({'status': 404})

    return JsonResponse({'status': 400})

def filter(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401}) 

    if request.method == 'GET':
        form = Filter(request.GET)
        if form.is_valid():
            data = form.cleaned_data
            page=data['page']
            maxPrice=data['maxPrice']
            minPrice=data['minPrice']
            category=data['category']
            searchText=data['searchText']
            
            print(searchText)
            if searchText==None or searchText=="":
                querySet=Product.objects.all()
            else:
                querySet=Product.objects.filter(name__icontains=searchText)

            if maxPrice!=-1:
                querySet =querySet & Product.objects.filter(price__lt=maxPrice)
            if minPrice!=-1:
                querySet =querySet & Product.objects.filter(price__gte=minPrice)
            if category!=-1:
                querySet =querySet & Product.objects.filter(category=category)

            p = Paginator(querySet.order_by("-dateCreated"), 20)

            try:
                contentPage = p.page(page).object_list
            except: 
                return JsonResponse({'status': 200, 'products':[]})

            json_data=[{
                'id':product.pk, 
                'title':product.name, 
                'description':product.description,
                'category':product.category.pk,
                'price':product.price,
                'profile':product.userSelling.pk,
                'date':product.dateCreated,
                'category_name':product.category.name,
                'category_name_pt':product.category.namePT,
                'profile_location':translateLocation(product.userSelling.cityX,product.userSelling.cityY),
                'profile_name':product.userSelling.user.username,
                'images':[productImage.image.url for productImage in ProductImage.objects.filter(product=product.pk)],
                'rating': computeRating(product.userSelling),
            } for product in contentPage]

            return JsonResponse({'status': 200, 'products':json_data})
        
    return JsonResponse({'status': 400})

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
        'category_name_pt':product.category.namePT,
        'profile_location':translateLocation(product.userSelling.cityX,product.userSelling.cityY),
        'profile_name':product.userSelling.user.username,
        'images':[productImage.image.url for productImage in ProductImage.objects.filter(product=product.pk)],
        'rating': computeRating(product.userSelling),
    } for product in page1]

    return JsonResponse({'status': 200, 'products':json_data})


def myProducts(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401}) 
    
    profile=Profile.objects.get(user=request.user.pk)
    ownProducts=Product.objects.filter(userSelling=profile.pk)
    json_data=[{
        'id':product.pk, 
        'title':product.name, 
        'description':product.description,
        'category':product.category.pk,
        'price':product.price,
        'profile':product.userSelling.pk,
        'date':product.dateCreated,
        'category_name':product.category.name,
        'category_name_pt':product.category.namePT,
        'profile_location':translateLocation(profile.cityX,profile.cityY),
        'profile_name':product.userSelling.user.username,
        'images':[productImage.image.url for productImage in ProductImage.objects.filter(product=product.pk)],
        'rating': computeRating(product.userSelling),
    } for product in ownProducts]

    return JsonResponse({'status': 200, 'products':json_data})








