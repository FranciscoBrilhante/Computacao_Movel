import os
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