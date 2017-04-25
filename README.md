# nvm-project
This project would need Celery on all nodes

Starting up a celery cluster:

```
cd ansible-rabbitmq
ansible-playbook -i hosts rabbitmq.yaml
```

tasks.py will need to be placed on the worker nodes and a Celery cluster needs to be setup for tasks to work.

Further instructions to be added
