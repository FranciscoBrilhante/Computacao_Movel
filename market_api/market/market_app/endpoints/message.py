from ..models import Profile, Message
from django.http import JsonResponse
from ..forms.message_forms import Send,GetNotificationToken
from ..forms.message_forms import GetByProfile as GetByProfileForm
import os
from django.utils import timezone
from ..utils import *
from django.db.models import Q

import requests
import json

import firebase_admin
from firebase_admin.credentials import Certificate
from firebase_admin.messaging import MulticastMessage, Notification, FCMOptions, AndroidConfig, BatchResponse
from firebase_admin.messaging import Message as FCMMessage
from firebase_admin import messaging

def send(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401})

    if request.method == 'POST':
        form = Send(request.POST)
        if form.is_valid():
            data = form.cleaned_data
            profile_pk = data['profile_id']

            user = request.user
            profile=Profile.objects.get(user=user.pk)
            
            if profile.pk==profile_pk:
                return JsonResponse({'status': 400})
            content=data['content']
            
            message=Message(userTo=Profile.objects.get(pk=profile_pk),userFrom=profile,content=content,dateSent=timezone.now())
            message.save()
            sendNotification(message=message)
            
            
            return JsonResponse({'status': 200})
            
        elif contains_error(form.errors.as_data(), 'resource not found'):
                return JsonResponse({'status': 404})

    return JsonResponse({'status': 400})


def getAll(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401})

    user=request.user
    profile=Profile.objects.get(user=user.pk)

    messages=Message.objects.filter(userTo=profile.pk)
    messages_received=[{'profile_id':message.userFrom.pk,'content':message.content,'timestamp':message.dateSent,'message_id':message.pk} for message in messages]

    messages=Message.objects.filter(userFrom=profile.pk)
    messages_sent=[{'profile_id':message.userTo.pk,'content':message.content,'timestamp':message.dateSent,'message_id':message.pk} for message in messages]


    return JsonResponse({'status': 200, 'received':messages_received,'sent':messages_sent})

def getByProfile(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401})

    if request.method == 'GET':
        form = GetByProfileForm(request.GET)
        if form.is_valid():
            data = form.cleaned_data
            profile_pk = data['profile_id']

            user = request.user
            profile=Profile.objects.get(user=user.pk)
            
            if profile.pk==profile_pk:
                return JsonResponse({'status': 400})
            
            messages=Message.objects.filter(userTo=profile.pk,userFrom=profile_pk)
            messages_received=[{'profile_id':message.userFrom.pk,'content':message.content,'timestamp':message.dateSent,'message_id':message.pk} for message in messages]

            messages=Message.objects.filter(userFrom=profile.pk,userTo=profile_pk)
            messages_sent=[{'profile_id':message.userTo.pk,'content':message.content,'timestamp':message.dateSent,'message_id':message.pk} for message in messages]

            return JsonResponse({'status': 200,'received':messages_received,'sent':messages_sent})
            
        elif contains_error(form.errors.as_data(), 'resource not found'):
                return JsonResponse({'status': 404})

    return JsonResponse({'status': 400})

def getProfilesWithMessages(request):    
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401})

    user=request.user
    profile=Profile.objects.get(user=user.pk)
    
    all_ids=[]
    messages=Message.objects.filter(userTo=profile.pk)
    for message in messages:
        if message.userFrom.pk not in all_ids:
            all_ids.append(message.userFrom.pk)
    messages=Message.objects.filter(userFrom=profile.pk)
    for message in messages:
        if message.userTo.pk not in all_ids:
            all_ids.append(message.userTo.pk)

    contacts=[]
    for id in all_ids:
        otherProfile=Profile.objects.get(pk=id)
        otherUser=otherProfile.user
        name=otherUser.username
        photo=otherProfile.photo
        if photo and hasattr(photo, 'url'):
            url=photo.url
        else:
            url=None
        
        queryset = Message.objects.filter(userTo=id).filter(userFrom=profile.pk) | Message.objects.filter(userTo=profile.pk).filter(userFrom=id)
        last_message=queryset.order_by('-dateSent')[0].content
        last_message_timestamp=queryset.order_by('-dateSent')[0].dateSent

        data={'profile_id':id,
                'profile_name':name,
                'profile_image':url,
                'last_message':last_message,
                'last_message_timestamp':last_message_timestamp
            }
        
        contacts.append(data)
    return JsonResponse({'status': 200,'contacts':contacts})


def setNotificationToken(request):
    if not request.user.is_authenticated:
        return JsonResponse({'status': 401})

    if request.method == 'POST':
        form = GetNotificationToken(request.POST)
        if form.is_valid():
            data = form.cleaned_data
            token = data['token']

            user=request.user
            profile=Profile.objects.get(user=user.pk)
            profile.notificationToken=token
            profile.save()
            return JsonResponse({'status': 200})

    return JsonResponse({'status': 400})


def sendNotification(message):
    profileTo=message.userTo
    username=message.userFrom.user.username
    message_content=message.content
    token=profileTo.notificationToken
    if token=="" or token==None:
        print("it tried")
        return 

    cert_data={
        "type": os.environ.get('type'),
        "project_id": os.environ.get('project_id'),
        "private_key_id": os.environ.get('private_key_id'),
        "private_key": os.environ.get('private_key'),
        "client_email": os.environ.get('client_email'),
        "client_id": os.environ.get('client_id'),
        "auth_uri": os.environ.get('auth_uri'),
        "token_uri": os.environ.get('token_uri'),
        "auth_provider_x509_cert_url": os.environ.get('auth_provider_x509_cert_url'),
        "client_x509_cert_url": os.environ.get('client_x509_cert_url')
    }

    try:
        certificate = Certificate(cert_data)
        firebase_admin.initialize_app(credential=certificate)
    except:
        pass

        notification=Notification(
                title=f"{username} sent you a message",
                body=f"{message_content}",
        )
        fcmMessage= FCMMessage(notification=notification, token=token)
    
    try:
        msg_id = messaging.send(fcmMessage)
    except Exception as e:
        print(e)
