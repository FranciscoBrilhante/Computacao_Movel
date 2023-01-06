from django import forms
from django.core.exceptions import ValidationError
from ..models import Profile,Category,Product,Report
from django.contrib.auth.models import User
from django.utils.translation import gettext as _

class Add(forms.Form):
    product_id = forms.IntegerField()
    reason=forms.CharField(max_length=64)
    explain=forms.CharField(max_length=2048,required=False)
    
    def clean_product_id(self):
        data = self.cleaned_data['product_id']
        if not Product.objects.filter(pk=data).exists():
            raise ValidationError(_('Product with that ID does not exist'),code='resource does not exist')
        return data


class ReportID(forms.Form):
    report_id = forms.IntegerField()

    def clean_report_id(self):
        data = self.cleaned_data['report_id']
        if not Report.objects.filter(pk=data).exists():
            raise ValidationError(_('Report with that ID does not exist'),code='resource does not exist')
        return data

class ProductID(forms.Form):
    product_id = forms.IntegerField()

    def clean_product_id(self):
        data = self.cleaned_data['product_id']
        if not Product.objects.filter(pk=data).exists():
            raise ValidationError(_('Product with that ID does not exist'),code='resource does not exist')
        return data