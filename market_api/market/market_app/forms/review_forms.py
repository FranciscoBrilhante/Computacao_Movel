from django import forms
from django.core.exceptions import ValidationError
from ..models import Profile,Category,Product
from django.contrib.auth.models import User
from django.utils.translation import gettext as _

class Add(forms.Form):
    profile_id = forms.IntegerField()
    stars=forms.IntegerField()

    def clean_profile_id(self):
        data = self.cleaned_data['profile_id']
        if not Profile.objects.filter(pk=data).exists():
            raise ValidationError(_('Profile with that ID does not exist'),code='resource does not exist')
        return data

    def clean_stars(self):
        data = self.cleaned_data['stars']
        if data<0 or data>10:
            raise ValidationError(_('Stars must be positive and bellow or equal to 10'),code='invalid')
        return data

class Score(forms.Form):
    profile_id = forms.IntegerField()
    def clean_profile_id(self):
        data = self.cleaned_data['profile_id']
        if not Profile.objects.filter(pk=data).exists():
            raise ValidationError(_('Profile with that ID does not exist'),code='resource does not exist')
        return data