#!/usr/bin/env python

""" fabfile.py: Fabric module to deploy the TIMBUS Capture Tool application."""

__author__  = "Ricardo F. Teixeira"
__email__   = "ricardo.teixeira@caixamagica.pt"

from fabric.api import *
from fabric.contrib.console import confirm
from fabric.colors import *
from datetime import date

env.roledefs = {
    'timbus': ['timbus-cms@134.191.240.76']
}

workspace = '/home/ricardo/workspace-sts/TIMBUS Capture Tool/'

@task
def compile():
    print(blue('Task: compile\n'))
    
    with cd(workspace):
        with settings(warn_only=True):
            result = local('mvn package', capture=True)
            
            if result.failed and not confirm("Something went wrong. Continue anyway?"):
                abort("Aborting at user request.")

@task
def deploy():
    print(blue('Task: tomcat\n'))
    
    with cd(workspace):
        with settings(warn_only=True):
            result = local('mvn clean tomcat:undeploy tomcat:deploy', capture=True)
            
            if result.failed and not confirm("Something went wrong. Continue anyway?"):
                abort("Aborting at user request.")
@task
@roles('timbus')
def backup():
    print(blue('Task: backup\n'))
    
    tmp = '/tmp/extractors-' + str(date.today()) + '.tar.gz'
    local('tar -czf ' + tmp + ' ' + workspace)
    put(tmp, '/home/timbus-cms/' + tmp)
