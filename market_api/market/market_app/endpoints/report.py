from ..models import *
from django.http import JsonResponse
from ..forms.report_forms import *
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
            product_id=data['product_id']
            reason=data['reason']
            explain=data['explain']

            product=Product.objects.get(pk=product_id)
            profile=Profile.objects.get(user=request.user.pk)

            report = Report(product=product,profile=profile,reason=reason,explain=explain)
            report.save()
            return JsonResponse({'status': 200})

        elif contains_error(form.errors.as_data(), 'resource not found'):
                return JsonResponse({'status': 404})

    return JsonResponse({'status': 400})

def remove(request):
    if not request.user.is_authenticated or not request.user.is_staff:
        return JsonResponse({'status': 401}) 
    if request.method == 'GET':
        form = ReportID(request.GET)
        if form.is_valid():
            data = form.cleaned_data
            report_id=data['report_id']
            
            report=Report.objects.get(pk=report_id)
            report.delete()
            return JsonResponse({'status': 200})

        elif contains_error(form.errors.as_data(), 'resource not found'):
                return JsonResponse({'status': 404})

    return JsonResponse({'status': 400})

def getAllByProduct(request):
    if not request.user.is_authenticated or not request.user.is_staff:
        return JsonResponse({'status': 401}) 
    if request.method == 'GET':
        form = ReportID(request.GET)
        if form.is_valid():
            data = form.cleaned_data
            product_id=data['product_id']
            
            product=Product.objects.get(pk=product_id)
            reports=Report.objects.filter(product=product)

            json_data=[{
                'report_id':report.pk,
                'product_id':product_id,
                'profile_id':report.profile.pk,
                'reason':report.reason,
                'explain':report.explain,
            } for report in reports]
            return JsonResponse({'status': 200, 'reports':json_data})

        elif contains_error(form.errors.as_data(), 'resource not found'):
                return JsonResponse({'status': 404})

    return JsonResponse({'status': 400})

def clearReports(request):
    if not request.user.is_authenticated or not request.user.is_staff:
        return JsonResponse({'status': 401}) 
    if request.method == 'GET':
        form = ReportID(request.GET)
        if form.is_valid():
            data = form.cleaned_data
            product_id=data['product_id']
            
            product=Product.objects.get(pk=product_id)
            reports=Report.objects.filter(product=product)

            reports.delete()
            return JsonResponse({'status': 200})

        elif contains_error(form.errors.as_data(), 'resource not found'):
                return JsonResponse({'status': 404})

    return JsonResponse({'status': 400})