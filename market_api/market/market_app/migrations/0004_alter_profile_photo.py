# Generated by Django 4.1.3 on 2022-12-01 10:14

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ("market_app", "0003_alter_profile_cityx_alter_profile_cityy_and_more"),
    ]

    operations = [
        migrations.AlterField(
            model_name="profile",
            name="photo",
            field=models.ImageField(blank=True, upload_to="profile_pics"),
        ),
    ]
