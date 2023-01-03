from django.db import models
from django.conf import settings

class Profile(models.Model):
    user = models.ForeignKey(settings.AUTH_USER_MODEL, on_delete=models.CASCADE)
    photo = models.ImageField(upload_to='profile_pics',blank=True)
    cityX=models.FloatField()
    cityY=models.FloatField()
    notificationToken=models.CharField(blank=True, null=True, max_length=1024)
    
class Message(models.Model):
    userTo=models.ForeignKey(Profile,on_delete=models.CASCADE,related_name="messages_received")
    userFrom=models.ForeignKey(Profile,on_delete=models.CASCADE,related_name="messages_sent")
    content=models.TextField()
    dateSent=models.DateTimeField()

class Category(models.Model):
    name=models.CharField(max_length=64)
    namePT=models.CharField(max_length=64)

class Review(models.Model):
    stars=models.FloatField()
    userReviewed=models.ForeignKey(Profile,on_delete=models.CASCADE,related_name="reviews_received")
    userReviewer=models.ForeignKey(Profile,on_delete=models.CASCADE,related_name="reviews_sent")
    dateReviewed=models.DateTimeField()


class Product(models.Model):
    name=models.CharField(max_length=64)
    description=models.CharField(max_length=1024)
    userSelling=models.ForeignKey(Profile,on_delete=models.CASCADE)
    price=models.FloatField()
    category=models.ForeignKey(Category,on_delete=models.CASCADE)
    dateCreated=models.DateTimeField()


class ProductImage(models.Model):
    product=models.ForeignKey(Product,on_delete=models.CASCADE)
    image=models.ImageField(upload_to="product_pics")


