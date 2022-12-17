from django import forms
from django.core.exceptions import ValidationError
from ..models import Profile,Category,Product
from django.contrib.auth.models import User
from django.utils.translation import gettext as _


class Add(forms.Form):
    name = forms.CharField(max_length=64)

    def clean_name(self):
        data = self.cleaned_data['name']
        if Category.objects.filter(name=data).exists():
            raise ValidationError(_('Category with that ID does not exist'),code='confict')
        return data

class Delete(forms.Form):
    category_id = forms.IntegerField()

    def clean_category_id(self):
        data = self.cleaned_data['category_id']
        if not Category.objects.filter(pk=data).exists():
            raise ValidationError(_('Category with that ID does not exist'),code='resource does not exist')
        return data
