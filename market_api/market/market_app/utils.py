import os
from geopy.geocoders import Nominatim
from .models import *
import pickle

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
        return None

    table={}
    try:
        with open('coordinates_table.pkl', 'rb') as f:
            table = pickle.load(f)
    except:
        pass

    entry=table.get(f"{cityY}, {cityX}",None)
    if entry==None:
        geolocator = Nominatim(user_agent="market_api_CM")
        location = geolocator.reverse(f"{cityY}, {cityX}")
        if location!=None:
            entry=location.raw.get('address',None)
            if len(table.keys())<100000:
                table[f"{cityY}, {cityX}"]=entry
                with open('coordinates_table.pkl', 'wb') as f:
                    pickle.dump(table, f)
        
    if(entry!=None):
        municipality=entry.get('municipality',None)
        county=entry.get('county',None)
        if(municipality!=None and county!=None):
            return f"{municipality}, {county}"
        elif(county!=None):
            return f"{county}"
        else:
            return None
    return None


def computeRating(profile): 
    reviews=Review.objects.filter(userReviewed=profile)  
    scores=[review.stars for review in reviews]
    if len(scores)==0:
        score=0
    else:
        score=sum(scores)/len(scores)

    return score