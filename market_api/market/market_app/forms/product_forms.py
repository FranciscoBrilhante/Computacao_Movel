from django import forms
from django.core.exceptions import ValidationError
from ..models import Profile,Category,Product
from django.contrib.auth.models import User
from django.utils.translation import gettext as _

class Add(forms.Form):
    title = forms.CharField(max_length=128)
    description = forms.CharField(max_length=1024)
    price=forms.FloatField()
    category=forms.IntegerField()

    def clean_price(self):
        data = self.cleaned_data['price']
        if data<=0:
            raise ValidationError(_('Price must be above 0€'),code='invalid')
        return data

    def clean_category(self):
        data = self.cleaned_data['category']
        if not Category.objects.filter(pk=data).exists():
            raise ValidationError(_('Category with that ID does not exist'),code='resource does not exist')
        return data

class ProductId(forms.Form):
    product_id=forms.IntegerField()

    def clean_product_id(self):
        data = self.cleaned_data['product_id']
        if not Product.objects.filter(pk=data).exists():
            raise ValidationError(_('Product with that ID does not exist'),code='resource does not exist')
        return data

class Photo(forms.Form):
    photo=forms.ImageField()
    product_id=forms.IntegerField()

    def clean_product_id(self):
        data = self.cleaned_data['product_id']
        if not Product.objects.filter(pk=data).exists():
            raise ValidationError(_('Product with that ID does not exist'),code='resource does not exist')
        return data

        

