from django import forms
from django.core.exceptions import ValidationError
from ..models import Profile
from django.contrib.auth.models import User
from django.utils.translation import gettext as _

class Register(forms.Form):
    username = forms.CharField(max_length=64)
    email = forms.CharField(max_length=256)
    password=forms.CharField(max_length=256)

    def clean_username(self):
        data = self.cleaned_data['username']
        if User.objects.filter(username=data).exists():
            raise ValidationError(_('User with that username already exists'),code='conflict')
        return data

    def clean_email(self):
        data = self.cleaned_data['email']
        if User.objects.filter(username=data).exists():
            raise ValidationError(_('User with that username already exists'),code='conflict')
        return data

class RegisterAdmin(forms.Form):
    username = forms.CharField(max_length=64)
    email = forms.CharField(max_length=256)
    password=forms.CharField(max_length=256)
    admin_key=forms.CharField(max_length=256)

    def clean_username(self):
        data = self.cleaned_data['username']
        if User.objects.filter(username=data).exists():
            raise ValidationError(_('User with that username already exists'),code='conflict')
        return data

    def clean_email(self):
        data = self.cleaned_data['email']
        if User.objects.filter(username=data).exists():
            raise ValidationError(_('User with that username already exists'),code='conflict')
        return data
        

class Login(forms.Form):
    username = forms.CharField(max_length=64)
    password=forms.CharField(max_length=256)

class Location(forms.Form):
    cityX=forms.FloatField()
    cityY=forms.FloatField()

class Photo(forms.Form):
    profile_photo=forms.ImageField()
    
class ProfileId(forms.Form):
    profile_id=forms.IntegerField()

    def clean_profile_id(self):
        data = self.cleaned_data['profile_id']
        if not Profile.objects.filter(pk=data).exists():
            raise ValidationError(_('Profile with that ID does not exist'),code='resource does not exist')
        return data