from myboto import ec2_conn

reservation = ec2_conn.run_instances('ami-00003837',
                key_name='demo',
                instance_type='m1.small',
                security_groups=['default']
            )

instance = reservation.instances[0]
print('New instance {} has been created.'.format(instance.id))

