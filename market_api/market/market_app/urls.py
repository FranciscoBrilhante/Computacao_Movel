from django.urls import path
from .endpoints import profile,message,database,product,product_category,review,report

urlpatterns = [
    path('profile/register', profile.register),
    path('profile/registeradmin', profile.registerAdmin),
    path('profile/login', profile.login),
    path('profile/setlocation', profile.setLocation),
    path('profile/setphoto', profile.setPhoto),
    path('profile/personalinfo', profile.personalInfo),
    path('profile/delete', profile.delete),
    path('profile/info', profile.info),

    path('message/send', message.send),
    path('message/all', message.getAll),
    path('message/withuser', message.getByProfile),
    path('message/users', message.getProfilesWithMessages),
    path('message/token', message.setNotificationToken),

    path('product/add',product.add),
    path('product/addphoto',product.addPhoto),
    path('product/delete',product.delete),
    path('product/details',product.details),
    path('product/filter',product.filter),
    path('product/recommended',product.recommended),
    path('product/myproducts',product.myProducts),

    path('category/add',product_category.add),
    path('category/delete',product_category.delete),
    path('category/all',product_category.getAll),

    path('review/add',review.add),
    path('review/score',review.score),
    path('review/privatescore',review.privateScore),
    path('review/reviewgiven',review.reviewGiven),

    path('report/add',report.add),
    path('report/byproduct',report.getAllByProduct),
    path('report/remove',report.remove),
    path('report/clear',report.clearReports),
    path('report/allproductsreported',report.getAllProductsWithReports),

    path('database/clear', database.clear),
    path('database/loginuser', database.login),
]