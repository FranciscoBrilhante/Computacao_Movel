from django.contrib import admin
from django.urls import include,path


from .endpoints import profile,message,database

urlpatterns = [
    path('profile/register', profile.register, name='register'),
    path('profile/login', profile.login, name='login'),
    path('profile/setlocation', profile.setLocation, name='setlocation'),
    path('profile/setphoto', profile.setPhoto, name='setphoto'),
    path('profile/personalinfo', profile.personalInfo, name='personalinfo'),
    path('profile/profilephoto', profile.profilePhoto, name='profilephoto'),
    path('profile/delete', profile.delete, name='delete'),
    path('database/clear', database.clear, name='clear'),
]