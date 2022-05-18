# User Management Service

Concierge User Management Service is a collection of endpoints to manage debit accounts and all movements related to specific account.

## Features

- Create new users
- Create locations for the administrations
- Create administrations for users

## Environment Configuration

For localhost, the recommendation is to create following components:

| Component                       | Details                                  |
|---------------------------------|------------------------------------------|
| PostgresQL v14                  | Database in master-replica mode          |
| DockerL v10.03.9                | Docker engine to execute image instances |
| User Management Service | Java code for the service                |

## Configuring User Management service in localhost

>**Pre-requisites:**
`Docker` is required for almost all the configurations in localhost, please make sure you have installed and configured Docker in your environment.

## _Option 1:_
### Running User Management service using only one database.

Execute in localhost following command:
```sh
docker network create concierge
docker run -d --name postgres-db-master -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=concierge-user-management -p 5432:5432 --net concierge postgres:14-alpine
```
The Debit Account Service will requirer following arguments configured in your IDE:
```sh
-DDEBIT_ACCOUNTS_DB_USER=postgres
-DDEBIT_ACCOUNTS_DB_PASSWORD=postgres
-DDEBIT_ACCOUNTS_DB_NAME=concierge-debit-accounts
-DDEBIT_ACCOUNTS_DB_READ_WRITE_HOST=localhost
-DDEBIT_ACCOUNTS_DB_READ_WRITE_PORT=5432
-DDEBIT_ACCOUNTS_DB_READ_ONLY_HOST=localhost
-DDEBIT_ACCOUNTS_DB_READ_ONLY_PORT=5432
```
## _Option 2:_
### Running Debit Account service using Read- Write and Read-Only database instances.
>**Note:**
This is a preferred option over  the option 1 because this configuration allows to
developers verify the use of transactional scopes based on the need of the query

>**Note:**
>- **master** instance is the **Read-Write** instance, for this document master will be a synonymous for **postgres-db-master**.
>- **replica** instance is the **Read-Only** instance, for this document master will be a synonymous for **postgres-db-replica**.

Execute in localhost following commands:
```sh
docker network create concierge
```
```sh
docker run -d --name postgres-db-master -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=concierge-debit-accounts -p 5435:5432 --net concierge postgres:14-alpine
```
```sh
docker run -d --name postgres-db-replica -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=concierge-debit-accounts -p 5434:5432 --net concierge postgres:14-alpine
```
Access to **master** instance:
```sh
docker exec -it postgres-db-master bash
```
Open the postgres configuration file:
```sh
vi var/lib/postgresql/data/postgresql.conf
```

>**Find the following keys and set the values as follows:**
>- wal_level=replica
>- max_wal_senders=2
>- archive_mod=on
>- archive_command='cp %p /tmp/%f'

Save and close the file.

Open the postgres configuration file:
```sh
vi var/lib/postgresql/data/pg_hba.conf
```
>**Append following line at the bottom:**
>- host replication all `{DOCKER_CIDR_REPLICA_INSTANCE}` trust

Where
`{DOCKER_CIDR_REPLICA_INSTANCE}` should be replaced with the value retrieved from the following command:

```sh
docker network inspect concierge -f '{{json .Containers}}' | jq '.'
```
>**Note:**
Use the value from **IPv4Address** from **postgres-db-replica** instance.

Save and close the file.

Finally, restart **master** instance using this:

```sh
docker restart postgres-db-master
```

Verify that **master** is up and running after restarting:

```sh
docker ps | grep postgres-db-master
```

Access to **replica** instance:
```sh
docker exec -it postgres-db-replica bash
```
Execute following command:

```sh
rm -rf /var/lib/postgresql/data/* && pg_basebackup -U postgres -R -D /var/lib/postgresql/data/ --host={DOCKER_GATEWAY_CONCIERGE_NETWORK} --port={PORT_MASTER}
```
Where
`{PORT_MASTER}` was defined as **5435** on creating the instance
`{DOCKER_GATEWAY_CONCIERGE_NETWORK}` should be replaced with the value retrieved from the following command:
```sh
docker network inspect concierge -f '{{json .IPAM.Config}}' | jq '.'
```
>**Note:**
Use the value from **Gateway** property.

Open the postgres configuration file:
```sh
vi var/lib/postgresql/data/postgresql.conf
```

>**Find the following keys and set the values as follows:**
>- hot_standby=on

Save and close the file.

Finally, restart **replica** instance using this:

```sh
docker restart postgres-db-replica
```

Verify that **replica** is up and running:

```sh
docker ps | grep postgres-db-replica
```


The Debit Account Service will requirer following arguments configured in your IDE:
```sh
-DDEBIT_ACCOUNTS_DB_USER=postgres
-DDEBIT_ACCOUNTS_DB_PASSWORD=postgres
-DDEBIT_ACCOUNTS_DB_NAME=concierge-debit-accounts
-DDEBIT_ACCOUNTS_DB_READWRITE_HOST=`{DOCKER_GATEWAY_CONCIERGE_NETWORK}`
-DDEBIT_ACCOUNTS_DB_READ_WRITE_PORT=`{PORT_MASTER}`
-DDEBIT_ACCOUNTS_DB_READONLY_HOST=`{DOCKER_GATEWAY_CONCIERGE_NETWORK}`
-DDEBIT_ACCOUNTS_DB_READ_ONLY_PORT=`{PORT_REPLICA}`
```
Where
`{PORT_MASTER}` was defined as **5435** on creating the instance
`{PORT_MASTER}` was defined as **5434** on creating the instance
`{DOCKER_GATEWAY_CONCIERGE_NETWORK}` is the value used above


## Create Docker Image for Debit Account service

Clone the project using following command:
```sh
git clone git@github.com:ngineapps/concierge-accounts.git
```
Create the artifact from root location of the project, at same level as the pom.xml file:
```sh
mvn clean package
```
Create image:
```sh
docker build -t ngineapps/concierge-debit-accounts .
```
Run the image:
>**Note:**
This example is using values from the Option 2 configuration, feel free to use the correct values according to your local configuration.

```sh
docker run \
--name debit-accounts-service \
-p 8080:8080 \
-e DEBIT_ACCOUNTS_DB_USER=postgres \
-e DEBIT_ACCOUNTS_DB_PASSWORD=postgres \
-e DEBIT_ACCOUNTS_DB_NAME=concierge-debit-accounts \
-e DEBIT_ACCOUNTS_DB_READ_WRITE_HOST={DOCKER_GATEWAY_CONCIERGE_NETWORK} \
-e DEBIT_ACCOUNTS_DB_READ_WRITE_PORT={PORT_MASTER} \
-e DEBIT_ACCOUNTS_DB_READ_ONLY_HOST={DOCKER_GATEWAY_CONCIERGE_NETWORK} \
-e DEBIT_ACCOUNTS_DB_READ_ONLY_PORT={PORT_REPLICA} \
ngineapps/concierge-debit-accounts
```

>**Note:**
You should see the log of the service with the message similar to this one:
`Started ConciergeAccountsApplication in 9.841 seconds (JVM running for 10.596)`

>**Note:**
Use this flag **-d** for the docker run command to run the service in detach mode.

Run the Keycloak:
docker run --name keycloak --rm -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin -p 8090:8080 -v keycloak:/opt/jboss/keycloak/standalone/data jboss/keycloak:10.0.2service

***EOF***
