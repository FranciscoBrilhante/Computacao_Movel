from ..models import Profile, Message
from django.http import JsonResponse
from ..forms.message_forms import Send
from ..forms.message_forms import GetByProfile as GetByProfileForm
import os
from django.utils import timezone
from ..utils import *
from django.db.models import Q

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
    messages_received=[{'profile_id':message.userFrom.pk,'content':message.content,'timestamp':message.dateSent} for message in messages]

    messages=Message.objects.filter(userFrom=profile.pk)
    messages_sent=[{'profile_id':message.userTo.pk,'content':message.content,'timestamp':message.dateSent} for message in messages]


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
            messages_received=[{'profile_id':message.userFrom.pk,'content':message.content,'timestamp':message.dateSent} for message in messages]

            messages=Message.objects.filter(userFrom=profile.pk,userTo=profile_pk)
            messages_sent=[{'profile_id':message.userTo.pk,'content':message.content,'timestamp':message.dateSent} for message in messages]

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
        
        query = Q(userTo=id)
        query.add(Q(userFrom=profile.pk), Q.AND)
        query.add(Q(userTo=profile.pk) , Q.OR)
        query.add(Q(userFrom=id), Q.AND)
        queryset = Message.objects.filter(userTo=id).filter(userFrom=profile.pk) | Message.objects.filter(userTo=profile.pk).filter(userFrom=id)
        last_message=queryset.order_by('-dateSent')[0].content

        data={'profile_id':id,
                'profile_name':name,
                'profile_image':url,
                'last_message':last_message,
            }
        
        contacts.append(data)
    return JsonResponse({'status': 200,'contacts':contacts})


