from django import forms
from django.core.exceptions import ValidationError
from ..models import Profile
from django.contrib.auth.models import User
from django.utils.translation import gettext as _

class Send(forms.Form):
    profile_id = forms.IntegerField()
    content=forms.CharField(max_length=512)

    def clean_profile_id(self):
        data = self.cleaned_data['profile_id']
        if not Profile.objects.filter(pk=data).exists():
            raise ValidationError(_('User with that id does not exist'),code='resource not found')
        return data

class GetByProfile(forms.Form):
    profile_id=forms.IntegerField()

    def clean_profile_id(self):
        data = self.cleaned_data['profile_id']
        if not Profile.objects.filter(pk=data).exists():
            raise ValidationError(_('User with that id does not exist'),code='resource not found')
        return data

class GetNotificationToken(forms.Form):
    token=forms.CharField()
    