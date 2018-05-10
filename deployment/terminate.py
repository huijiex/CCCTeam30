from myboto import ec2_conn
from boto import beanstalk

INSTANCE_ID = '6a1e8836-3774-4a11-bf56-ddd27f904314'

ec2_conn.terminate_instances(instance_ids=[INSTANCE_ID])

