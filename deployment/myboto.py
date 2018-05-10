import boto
from boto.ec2.regioninfo import RegionInfo
from pprint import pprint

region = RegionInfo(name='melbourne', endpoint='nova.rc.nectar.org.au')

ec2_conn = boto.connect_ec2(aws_access_key_id='44b68aef40eb4db8a02fe3ae8f399fdf',
                            aws_secret_access_key='e4e6b24ff74e4932a05e0eb90ee2750d',
                            is_secure=True,
                            region=region,
                            port=8773,
                            path='/services/Cloud',
                            validate_certs=False
                            )

