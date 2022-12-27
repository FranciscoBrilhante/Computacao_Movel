import os
from geopy.geocoders import Nominatim
from .models import *

#_________________ Helpers ___________________#
def contains_error(errors, code):
    for key, values in errors.items():
        for value in values:
            if(value.code == code):
                return True
    return False


def get_first_error_code(errors):
    for key, values in errors.items():
        for value in values:
            return value.code
    return None


def try_delete_file(path):
    try:
        os.remove(path)
    except:
        pass

def translateLocation(cityX,cityY):
    if(cityX==0 and cityY==0):
        return "null"

    geolocator = Nominatim(user_agent="market_api_CM")
    location = geolocator.reverse(f"{cityY}, {cityX}")

    address=location.raw.get('address',None)
    if(address!=None):
        municipality=address.get('municipality',None)
        county=address.get('county',None)
        if(municipality!=None and county!=None):
            return f"{municipality}, {county}"
        elif(county!=None):
            return f"{county}"
        else:
            return "null"
    return "null"


def computeRating(profile):
    reviews=Review.objects.filter(userReviewed=profile)  
    scores=[review.stars for review in reviews]
    if len(scores)==0:
        score=0
    else:
        score=sum(scores)/len(scores)

    return score