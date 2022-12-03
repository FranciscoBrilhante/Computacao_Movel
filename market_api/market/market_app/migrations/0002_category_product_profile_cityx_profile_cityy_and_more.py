# Generated by Django 4.1.3 on 2022-11-30 23:49

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ("market_app", "0001_initial"),
    ]

    operations = [
        migrations.CreateModel(
            name="Category",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("name", models.CharField(max_length=64)),
            ],
        ),
        migrations.CreateModel(
            name="Product",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("name", models.CharField(max_length=64)),
                ("description", models.CharField(max_length=1024)),
                ("price", models.FloatField()),
                (
                    "category",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="market_app.category",
                    ),
                ),
            ],
        ),
        migrations.AddField(
            model_name="profile",
            name="cityX",
            field=models.FloatField(default=0),
        ),
        migrations.AddField(
            model_name="profile",
            name="cityY",
            field=models.FloatField(default=0),
        ),
        migrations.AddField(
            model_name="profile",
            name="photo",
            field=models.ImageField(default=None, upload_to="profile_pics"),
        ),
        migrations.CreateModel(
            name="Review",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("stars", models.FloatField()),
                ("dateReviewed", models.DateTimeField()),
                (
                    "userReviewed",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        related_name="reviews_received",
                        to="market_app.profile",
                    ),
                ),
                (
                    "userReviewer",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        related_name="reviews_sent",
                        to="market_app.profile",
                    ),
                ),
            ],
        ),
        migrations.CreateModel(
            name="ProductImage",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("image", models.ImageField(upload_to="product_pics")),
                (
                    "product",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        to="market_app.product",
                    ),
                ),
            ],
        ),
        migrations.AddField(
            model_name="product",
            name="userSelling",
            field=models.ForeignKey(
                on_delete=django.db.models.deletion.CASCADE, to="market_app.profile"
            ),
        ),
        migrations.CreateModel(
            name="Message",
            fields=[
                (
                    "id",
                    models.BigAutoField(
                        auto_created=True,
                        primary_key=True,
                        serialize=False,
                        verbose_name="ID",
                    ),
                ),
                ("content", models.TextField()),
                ("dateSent", models.DateTimeField()),
                (
                    "userFrom",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        related_name="messages_sent",
                        to="market_app.profile",
                    ),
                ),
                (
                    "userTo",
                    models.ForeignKey(
                        on_delete=django.db.models.deletion.CASCADE,
                        related_name="messages_received",
                        to="market_app.profile",
                    ),
                ),
            ],
        ),
    ]
