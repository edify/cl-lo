# cl-lo

Common Library Learning Object

## Build

1. Everything you need to compile this project is written in the build.gradle. Compile the project using gradle wrapper:

        $ ./gradlew build

## Run

1. You need to run the docker compose file first.

  -  In the project root you will find a docker-compose.yml file with the required images. It's currently downloading the latest versions:

    | **Image**                 | **Used by**                  |
    | -------                   | -------                      |
    | OrientDB: 2.2.6           | cl-curricula                 |
    | ElasticSearch: 2.3.4      | cl-lo, cl-index              |
    | Mongo: 3.2.8              | cl-lo                        |
    | Redis: 3.0                | cl-auth                      |
    | RabbitMQ: 3.6.5           | cl-lo, cl-index.             |
	

  -  To start up the databases using docker:

```bash
$ export ORIENTDB_ROOT_PASSWORD=root
$ docker-compose up -d
```
        

2. Then you need to add the following environment variables to your system:

  -  AWS_ACCESS_KEY
  -  AWS_SECRET_KEY
  -  AWS_S3_BUCKET_NAME
  -  CERT_KEY_STORE_PATH
  -  CERT_KEY_STORE_PW
  -  CERT_KEY_PW
  -  CL_AUTH_PASSPHRASE
  -  CL_REDIS_HOST
  -  CL_REDIS_PORT

Notes:
  - The CL_AUTH_PASSPHRASE must be the same that you set in the cl-auth bootstrap. Required for secretKey decryption.


3.  If you don't have a self signed certificate, you can create one with the following command (remember to change argument values):

```bash
$ keytool -genkey -keyalg RSA -dname "cn=cn, ou=ou, o=o, c=IN" -alias anyAlias -keystore pathToStoreTheKey -storepass anyPassword -validity 3600 -keysize 2048
```


4.  The following profiles define the desired database administrators and the message broker:

  -  BE_Mongo
  -  FS_S3
  -  SRCH_ES
  -  INDEX_RMQ


5.  Run the jar file (This project requires Java 8):

```bash
$ java -Dspring.profiles.active=BE_Mongo,FS_S3,SRCH_ES,INDEX_RMQ -jar build/libs/*.jar
```

---

# **Learning Object REST API Reference**

This section covers the main concepts of the Common Library's Learning Object REST API. It will explain how the authentication works, as well as the available resources and their format. The allowed operations on the resorces are going to be detailed with command line examples using [cUrl](https://curl.haxx.se/docs/manpage.html).

# Base URL

For development purposes, the base URL will be pointing by default to the server that is running the cl-api application with the port 8443. This could be customized by editing the application.yaml of the cl-api project. Besides that, the URL contains /api/v1 after the host name. Example:

        https://localhost:8443/api/v1

# Resource Format

This API uses mainly [JSON](http://www.w3schools.com/json/) to represent the resources.

# Authentication

This project uses the [Stormpath signature algorithm](https://github.com/stormpath/stormpath-sdk-spec/blob/master/specifications/algorithms/sauthc1.md) to authenticate requests.

An example of the required headers is shown below:

```
Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160904/0tjqOB9pVP/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=9207def3db7c144f03fb148e35ab461a0be308bd518f51c15112ab1067d4fa5b
X-Stormpath-Date: 20160904T051810Z
content-type: application/json
```

All the cUrl examples that will be shown need those headers in order to perform the requests successfully. Inside the cl-auth/cl-auth-js project there is a command line interface script that can generate the required headers so you can use them in cUrl.

Sauthc1_cli example:

1.  First you need to set environment variables. These values can be obtained by running the cl-auth bootstrap script.

  - CL_API_CLIENT_ID
  - CL_API_CLIENT_SECRET

2. Make sure you already installed all the NodeJS dependencies inside the cl-auth-js project:

```bash
$ cd ../cl-auth/cl-auth-js
$ npm install
$ chmod +x bin/sauthc1_cli
```

3. Use the script by passing the required arguments:

```bash
$ ./bin/sauthc1_cli --url https://localhost:8443/api/v1/learningObjectives \
--method POST \
--body '{
        "name": "LO Name",
        "description": "LO Description",
        "learningObjectiveList": []
       }' \
--id ${CL_API_CLIENT_ID} \
--secret ${CL_API_CLIENT_SECRET}
```

The previous command will output a string:

```
{ Host: 'localhost:8443',
  'X-Stormpath-Date': '20160905T154832Z',
  Authorization: 'SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/uYHxdluS6Q/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=9111371712bdf41917303d638ac39c4d4b8099017a32704e9fc36911c4915f13' }
```

Then, you can use those values to build a cUrl request:

```bash
curl -k --request POST \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/uYHxdluS6Q/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=9111371712bdf41917303d638ac39c4d4b8099017a32704e9fc36911c4915f13" \
--header "X-Stormpath-Date: 20160905T154832Z" \
--header "content-type: application/json" \
--url "https://localhost:8443/api/v1/learningObjectives" \
--data '{
        "name": "LO Name",
        "description": "LO Description",
        "learningObjectiveList": []
       }'
```

# CRUD on resources

The next section gives the user a complete guide about how to create, retrieve, update and delete resources.

---

## Response Status Codes

It's important that you know all the response status codes and their meaning:  


| **Response code** | **Description**                                                                               |
| -------           | -------                                                                                       |
| 200               | The request was successful and the response body contains the expected data.                  |
| 400               | The submitted data failed validation.                                                         |
| 401               | Authentication credentials are required to access the resource.                               |
| 404               | The requested resource was not found.                                                         |
| 405               | The method is not allowed in the specified resource.                                          |
| 415               | Unsupported Media Type.                                                                       |
| 500               | The server encountered an unexpected condition which prevented it from fulfilling the request.|                                                                                       |

---

## REST Error Response

When the response status code is not 200, you will get a different response body:  

  a. Custom errors:

| **Attribute**     | **Description**                                                                                                       |
| -------           | -------                                                                                                               |
| code              | A Common Library-specific error code that can be used to obtain specific information.                                 |
| message           | An understandable user friendly message explaining what happened with the request.                                    |
| description       | Short text containing a unique error identifier that may be used to obtain more information from the CL developers.   |

JSON Example:

```json
{
  "code": "3001",
  "message": "Learning Objective with id: 57b7712e79988625a6b8f5e8 not found",
  "description": "Oops! something went wrong. Please use this code 31cc73102714464ce3ab9527b93b7036f6e5a5e736acaf8c6ba62c56b60c00a0 when reporting us the error."
}
```  

  b. Default errors:

| **Attribute**     | **Description**                                                                                                       |
| -------           | -------                                                                                                               |
| timestamp         | Time when the error was found.                                                                                        |
| status            | Response status code.                                                                                                 |
| error             | Message explaining what happened with the request.                                                                    |
| exception         | Exception's Class name.                                                                                               |
| message           | Message explaining what happened with the request with a more technical language.                                     |
| path              | Requested url.                                                                                                        |

JSON Example:

```json
{
  "timestamp": 1471827633370,
  "status": 415,
  "error": "Unsupported Media Type",
  "exception": "org.springframework.web.HttpMediaTypeNotSupportedException",
  "message": "Content type 'application/x-www-form-urlencoded;boundary=------------------------efd845251f7e6caf' not supported",
  "path": "/api/v1/learningObjects/57ba40039d789851ef6ac9fb/contents/57ba414a9d789851ef6ac9fc/file/"
}
```


## Available Resources

Before going trough all the Learning Object's related resources, you should know that all entities extends from BaseEntity, which has the following read only attributes:

- **id**: A generated unique identifier.                                    (String)
- **version**: How many times the entity has been modified.                 (Long)
- **creationDate**: Date when the entity was stored to the database.        (Date)
- **modificationDate**: Last time it was modified.                          (Date)

---

## **1. Learning Objective**

A learning objective is a brief statement that describes what people will be expected to learn after consuming a specific educational material.

## Resource URL

```
https://localhost:8443/learningObjectives/{id}
```

## Resource Attributes

- **name**: Learning objective's name.                                                  (String)
- **description**: Chunk of text describing the objective.                              (String)
- **learningObjectiveList**: List of relationships with others Learning Objectives.     (LearningObjective List)


Resource JSON example

```json
{
   "id":"57b88f5f2f5c80fe3ad7e4f6",
   "creationDate":1471713119057,
   "modificationDate":1471713119057,
   "name":"LO Name",
   "description":"LO Description",
   "learningObjectiveList":[]
}
```

## Learning Objective Operations

### Create a learning objective

- **HTTP_METHOD**: POST

- **URL**: https://localhost:8443/learningObjectives

- **Request data:**

  - Learning objective JSON.

- **Example Query:**

```bash
curl -k --request POST \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjectives" \
--data '{
        "name": "LO Name",
        "description": "LO Description",
        "learningObjectiveList": []
       }'
```

- **Example Response:**

```json
{
  "id": "57b88f5f2f5c80fe3ad7e4f6",
  "creationDate": 1471713119057,
  "modificationDate": 1471713119057,
  "name": "LO Name",
  "description": "LO Description",
  "learningObjectiveList": [],
  "new": false
}
```

---

### Retrieve a specific learning objective by id

- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/learningObjectives/{id}

- **Request data:** None.

- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjectives/57b88f5f2f5c80fe3ad7e4f6"
```

- **Example Response:**

```json
{
  "id": "57b891ec2f5c80fe3ad7e4f7",
  "creationDate": 1471713772137,
  "modificationDate": 1471713772137,
  "name": "LO Name",
  "description": "LO Description",
  "learningObjectiveList": [],
  "new": false
}
```

---

### Retrieve multiple learning objectives

- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/learningObjectives

- **Query Params:**

  - **from:** Initial index for the results. (Positive integer, 0-index)
  - **size:** How many learning objectives you wanna get from the starting index. (Positive integer)
  - **all:** If this option is set to true, from and size attributes are ignored and the response will contain all learning objectives in the database. (true/false)

- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjectives?from=0&size=5&all=false"
```

- **Example Response:**

```json
{
  "content": [
    {
      "id": "57b891ec2f5c80fe3ad7e4f7",
      "creationDate": 1471713772137,
      "modificationDate": 1471713772137,
      "name": "LO Name",
      "description": "LO Description",
      "learningObjectiveList": [],
      "new": false
    },
    {
      "id": "57b88e692f5c80fe3ad7e4f5",
      "creationDate": 1471712873465,
      "modificationDate": 1471712873465,
      "name": "New Learning Objective name",
      "description": "New Learning Objective description",
      "learningObjectiveList": [],
      "new": false
    },
    {
      "id": "57b586ff129cd5cf2b6317e7",
      "creationDate": 1471514367752,
      "modificationDate": 1471514367752,
      "name": "MAT133-07",
      "description": "Examine and explain the basic concepts of applied mathematics for business, finance, and engineering.",
      "learningObjectiveList": [],
      "new": false
    },
    {
      "id": "57b586ff129cd5cf2b6317e6",
      "creationDate": 1471514367741,
      "modificationDate": 1471514367741,
      "name": "MAT133-06",
      "description": "Demonstrate a basic understanding of differentiation and integration and their applications.",
      "learningObjectiveList": [],
      "new": false
    },
    {
      "id": "57b586ff129cd5cf2b6317e5",
      "creationDate": 1471514367729,
      "modificationDate": 1471514367729,
      "name": "MAT133-05",
      "description": "Examine and explain the value of probability and statistical methods.",
      "learningObjectiveList": [],
      "new": false
    }
  ],
  "firstPage": true,
  "lastPage": false,
  "totalPages": 2,
  "totalElements": 9,
  "last": false,
  "numberOfElements": 5,
  "sort": [
    {
      "direction": "DESC",
      "property": "modificationDate",
      "ignoreCase": false,
      "nullHandling": "NATIVE",
      "ascending": false
    }
  ],
  "first": true,
  "size": 5,
  "number": 0
}
```

---

### Update a learning objective

- **HTTP_METHOD**: PUT

- **URL**: https://localhost:8443/learningObjectives/{id}

- **Request data:**

  - Learning objective JSON.

- **Example Query:**

```bash
curl -k --request PUT \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjectives/57b88f5f2f5c80fe3ad7e4f6" \
--data '{
        "name": "Updated LO Name",
        "description": "Updated LO Description",
        "learningObjectiveList": []
       }'
```

- **Example Response:**

```json
{
  "id": "57b88f5f2f5c80fe3ad7e4f6",
  "creationDate": 1471713119057,
  "modificationDate": 1471713317762,
  "name": "Updated LO Name",
  "description": "Updated LO Description",
  "learningObjectiveList": [],
  "new": false
}
```

---

### Delete a learning objective

- **HTTP_METHOD**: DELETE

- **URL**: https://localhost:8443/learningObjectives/{id}

- **Request data:** None.

- **Example Query:**

```bash
curl -k --request DELETE \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjectives/57b88f5f2f5c80fe3ad7e4f6"
```

- **Example Response:**

```json
{
  "id": "57b88f5f2f5c80fe3ad7e4f6",
  "creationDate": 1471713119057,
  "modificationDate": 1471713317762,
  "name": "Updated LO Name",
  "description": "Updated LO Description",
  "learningObjectiveList": [],
  "new": false
}
```

## **2. Learning Object**

When we refer to learning objects, we are talking about any entity that can be used for learning, education or training. Besides that, a learning object seeks to fulfill one or more learning objectives.

## Resource URL

```
https://localhost:8443/learningObjects/{id}
```

## Resource Attributes

- **name**: Learning object's name. (String)
- **compound**: Indicates if the contents of the Learning Object is formed by multiple resources. (true/false)
- **subject**: Learning object's subject. (String)
- **description**: Chunk of text for general information about the learning object.
- **title**: Learning object's title. (String)
- **externalUrl**: If the Learning object contains an external url instead of a file, it will be stored in this attribute. (String).
- **type**: You can get the list of available types with the following request.

  * Example query:

      ```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/listTypes"
      ```

  * Example response:

      ```javascript
      ["ANY","EXERCISE","SIMULATION","QUESTIONNAIRE","DIAGRAM","FIGURE","GRAPH","INDEX","SLIDE",
      "TABLE","NARRATIVE_TEXT","EXAM","EXPERIMENT","PROBLEM_STATEMENT","SELF_ASSESSMENT"]
      ```

- **format**: You can get the list of available formats with the following request.

  * Example query:

      ```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/listFormats"
      ```

  * Example response:

      ```javascript
      ["IMAGE","HTML","XML","VIDEO","AUDIO","PLAIN_TEXT","JSON","URL","MULTIMEDIA",
      "PDF","EXCEL","POWER_POINT","WORD","ODS","ODP","ODT"]
      ```

- **metadata**: This is an object used to describe in depth the learning object. Its attributes are listed below:

  - **keywords**: Tags describing the learning object. (String)

  - **coverage**: Coverage of the object. (String)

  - **context**: Learning object's context. You can find the list of available contexts with the following request:

      - Example query:

          ```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/listContexts"
          ```

      - Example response:

          ```javascript
            ["ANY","PRIMARY_EDUCATION","SECONDARY_EDUCATION","HIGHER_EDUCATION","UNIVERSITY_FIRST_CYCLE",
            "UNIVERSITY_SECOND_CYCLE","UNIVERSITY_POSTGRADE","TECHNICAL_SCHOOL_FIRST_CYCLE",
            "TECHNICAL_SCHOOL_SECOND_CYCLE","PROFESSIONAL_FORMATION","CONTINUOUS_FORMATION","VOCATIONAL_TRAINING"]
          ```

  - **difficulty**: Learning object's material difficulty. You can find the list of available difficulties with the following request:

      - Example query:

          ```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/listDifficulties"
          ```

      - Example response:

          ```javascript
            ["ANY","VERY_LOW","LOW","MEDIUM","HIGH","VERY_HIGH"]
          ```

  - **endUser**: Learning object's intended user. You can find the list of available end users with the following request:

      - Example query:

          ```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/listIntendedUsers"
          ```

      - Example response:

          ```javascript
            ["ANY","AUTHOR","TEACHERS","LEARNERS"]
          ```

  - **interactivityDegree**: Learning object's level of interactivity. You can find the list of interactivity degrees with the following request:

      - Example query:

          ```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/listInteractivityDegrees"
          ```

      - Example response:

          ```javascript
            ["ANY","ACTIVE","MIXED","UNDEFINED","EXPOSITIVE","INTERACTIVE"]
          ```

  - **language**: Learning object's material language. You can find the list of languages with the following request:

      - Example query:

          ```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/listLanguages"
          ```

      - Example response:

          ```javascript
            ["Abkhazian","Afar","Afrikaans","Akan","Albanian","Amharic","Arabic","Aragonese","Armenian","Assamese",
            "Avaric","Avestan","Aymara","Azerbaijani","Bambara","Bashkir","Basque","Belarusian","Bengali","Bihari",
            "Bislama","Bosnian","Breton","Bulgarian","Burmese","Catalan","Chamorro","Chechen","Chichewa","Chinese",
            "ChurchSlavic","Chuvash","Cornish","Corsican","Cree","Croatian","Czech","Danish","Divehi","Dutch","Dzongkha",
            "English","Esperanto","Estonian","Ewe","Faroese","Fijian","Finnish","French","Fulah","Galician","Ganda",
            "Georgian","German","Greek","Guarani","Gujarati","Haitian","Hausa","Hebrew","Herero","Hindi","HiriMotu",
            "Hungarian","Icelandic","Ido","Igbo","Indonesian","Interlingua","Interlingue","Inuktitut","Inupiaq","Irish",
            "Italian","Japanese","Javanese","Kalaallisut","Kannada","Kanuri","Kashmiri","Kazakh","Khmer","Kikuyu",
            "Kinyarwanda","Kirghiz","Kirundi","Komi","Kongo","Korean","Kuanyama","Kurdish","Lao","Latin","Latvian",
            "Limburgish","Lingala","Lithuanian","LubaKatanga","Luxembourgish","Macedonian","Malagasy","Malay","Malayalam",
            "Maltese","Manx","Maori","Marathi","Marshallese","Moldavian","Mongolian","Nauru","Navajo","Ndonga","Nepali",
            "NorthernSami","NorthNdebele","Norwegian","NorwegianBokmal","NorwegianNynorsk","Occitan","Ojibwa","Oriya",
            "Oromo","Ossetian","Pali","Panjabi","Pashto","Persian","Polish","Portuguese","Quechua","RaetoRomance","Romanian",
            "Russian","Samoan","Sango","Sanskrit","Sardinian","ScottishGaelic","Serbian","SerboCroatian","Shona","SichuanYi",
            "Sindhi","Sinhala","Slovak","Slovenian","Somali","SouthernSotho","SouthNdebele","Spanish","Sundanese","Swahili",
            "Swati","Swedish","Tagalog","Tahitian","Tajik","Tamil","Tatar","Telugu","Thai","Tibetan","Tigrinya","Tonga",
            "Tsonga","Tswana","Turkish","Turkmen","Twi","Uighur","Ukrainian","Urdu","Uzbek","Venda","Vietnamese","Volapuk",
            "Walloon","Welsh","WesternFrisian","Wolof","Xhosa","Yiddish","Yoruba","Zhuang","Zulu"]
          ```

  - **status**: Learning object's status. You can find the list of statuses with the following request:

      - Example query:

          ```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/listStatuses"
          ```

      - Example response:

          ```javascript
            ["ANY","FINAL","REVISED","UNAVAILABLE","DRAFT","PUBLISHED"]
          ```

  - **author**: Learning object's author. (String)
  - **topic**: Learning object's topic. (String)
  - **isbn**: The International Standard Book Number unique book identifier. (String)
  - **price**: Learning object's material estimated price. (Positive number).

  - **extraMetadata**: Is a String list where you can add additional custom metadata. Example:

      ```javascript
        [
            "Additional metadata key,Additional metadata value",
            "Another metadata key,Another metadata value"
        ]
      ```

- **contents**: Object that describes the learning object's material (files and urls). Its attributes are listed below:

  - **md5**: Contents File's md5 encryption. (String)
  - **mimeType**: Two part identifier for describing a file format. (String)
  - **url**: URL pointing to the current version file.
  - **resourcesURL**: If your contents have additional related resources you can add here their URLs. (String list)

  - Example object:

      ```json
        {
          "id": "57ba1a909d789851ef6ac9fa",
          "creationDate": 1471814288366,
          "modificationDate": 1471814289368,
          "mimeType": "image/png",
          "md5": null,
          "url": "/learningObjects/57ba1a909d789851ef6ac9f9/contents/57ba1a909d789851ef6ac9fa/file/57ba1a909d789851ef6ac9f9_file?refPath=57ba1a909d789851ef6ac9f9/",
          "resourcesURL": [],
          "new": false
        }
      ```

- **enabled**: Wheter the learning object is enabled or not. (true/false)

Resource JSON example
```json
{
  "id": "57ba1a909d789851ef6ac9f9",
  "creationDate": 1471814288146,
  "modificationDate": 1471814289373,
  "name": "Learning Object's name",
  "compoundContent": false,
  "subject": "Learning Object's subject",
  "description": "Learning Object's description",
  "title": "Learning Object's title",
  "type": "EXERCISE",
  "format": "IMAGE",
  "metadata": {
    "keywords": null,
    "coverage": "Learning Object's coverage",
    "context": "PRIMARY_EDUCATION",
    "difficulty": "VERY_LOW",
    "endUser": "LEARNERS",
    "interactivityDegree": null,
    "language": "English",
    "status": "FINAL",
    "author": "Learning Object's author",
    "topic": null,
    "isbn": "Learning Object's ISBN",
    "price": 100,
    "extraMetadata": [
      "Additional metadata key,Additional metadata value"
    ]
  },
  "contents": {
    "id": "57ba1a909d789851ef6ac9fa",
    "creationDate": 1471814288366,
    "modificationDate": 1471814289368,
    "mimeType": "image/png",
    "md5": null,
    "url": "/learningObjects/57ba1a909d789851ef6ac9f9/contents/57ba1a909d789851ef6ac9fa/file/57ba1a909d789851ef6ac9f9_file?refPath=57ba1a909d789851ef6ac9f9/",
    "resourcesURL": [],
    "new": false
  },
  "enabled": true,
  "learningObjectiveList": [
    {
      "id": "57ba0f779d788470efe91b13",
      "creationDate": 1471811447274,
      "modificationDate": 1471811447274,
      "name": "ENG132-01",
      "description": "Analyze and synthesize texts and think critically to produce advanced academic discourse.",
      "learningObjectiveList": [],
      "new": false
    }
  ],
  "new": false
}
```

## Learning Object Operations

### Create a learning object

- **HTTP_METHOD**: POST

- **URL**: https://localhost:8443/learningObjects

- **Request data:**

  - Learning object JSON.

- **Example Query:**

```bash
curl -k --request POST \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects" \
--data '{
        "name": "Learning Object name",
        "subject": "Learning Object subject",
        "description": "Learning Object description",
        "title": "Learning Object title",
        "type": "EXERCISE",
        "format": "IMAGE",
        "metadata": {
        "coverage": "Learning Object coverage",
        "context": "PRIMARY_EDUCATION",
        "difficulty": "VERY_LOW",
        "endUser": "LEARNERS",
        "language": "English",
        "status": "FINAL",
        "author": "Learning Object author",
        "isbn": "Learning Object ISBN",
        "price": "100",
        "extraMetadata": [
          "Additional metadata key,Additional metadata value"
        ]
        },
        "enabled": true,
        "learningObjectiveList": [
        {
          "id": "57ba0f779d788470efe91b13",
          "creationDate": 1471811447274,
          "modificationDate": 1471811447274,
          "name": "ENG132-01",
          "description": "Analyze and synthesize texts and think critically to produce advanced academic discourse.",
          "learningObjectiveList": [],
          "new": false
        }
        ]
       }'
```

- **Example Response:**

```json
{
  "id": "57ba40039d789851ef6ac9fb",
  "creationDate": 1471823875451,
  "modificationDate": 1471823875451,
  "name": "Learning Object name",
  "compoundContent": false,
  "subject": "Learning Object subject",
  "description": "Learning Object description",
  "title": "Learning Object title",
  "type": "EXERCISE",
  "format": "IMAGE",
  "metadata": {
    "keywords": null,
    "coverage": "Learning Object coverage",
    "context": "PRIMARY_EDUCATION",
    "difficulty": "VERY_LOW",
    "endUser": "LEARNERS",
    "interactivityDegree": null,
    "language": "English",
    "status": "FINAL",
    "author": "Learning Object author",
    "topic": null,
    "isbn": "Learning Object ISBN",
    "price": 100,
    "extraMetadata": [
      "Additional metadata key,Additional metadata value"
    ]
  },
  "contents": null,
  "enabled": true,
  "learningObjectiveList": [
    {
      "id": "57ba0f779d788470efe91b13",
      "creationDate": 1471811447274,
      "modificationDate": 1471811447274,
      "name": "ENG132-01",
      "description": "Analyze and synthesize texts and think critically to produce advanced academic discourse.",
      "learningObjectiveList": [],
      "new": false
    }
  ],
  "new": false
}
```

---

### Create learning object's content

- **HTTP_METHOD**: POST

- **URL**: https://localhost:8443/learningObjects/{loId}/contents

- **Request data:**

  - Contents JSON.

- **Example Query:**

```bash
curl -k --request POST \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57ba40039d789851ef6ac9fb/contents" \
--data '{
        "md5": "MD5 String",
        "mimeType": "image/png",
        "url": ""
       }'
```

- **Example response:**

```json
{
  "id": "57ba414a9d789851ef6ac9fc",
  "creationDate": 1471824202255,
  "modificationDate": 1471824202255,
  "mimeType": "image/png",
  "md5": "MD5 String",
  "url": "",
  "resourcesURL": [],
  "new": false
}
```

### Create content's file by uploading a multipart file

- **HTTP_METHOD**: POST

- **URL**: https://localhost:8443/learningObjects/{loId}/contents/{contentsId}/file

- **Request data:**

This is one of the exceptions to the rule of sending data only in JSON format. This time you send a multipart/form-data with the following fields:

  - filename: The desired file's name. (String)
  - primaryType: File's primary type, example: image. (String)
  - secondaryType: File's secondary type, example: png. (String)
  - content: File to be uploaded. (Multipart file)
  - refPath: Path where the file will be stored in the filesystem. (String)

- **Example Query:**

```bash
curl -k --request POST \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: multipart/form-data" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57ba40039d789851ef6ac9fb/contents/57ba414a9d789851ef6ac9fc/file" \
     -F filename=desiredFilename \
     -F primaryType=image \
     -F secondaryType=png \
     -F content=@logo.png \
     -F refPath=57ba40039d789851ef6ac9fb/
```

- **Example response (FileResponse object):**

```json
{
  "url": "/learningObjects/57ba40039d789851ef6ac9fb/contents/57ba414a9d789851ef6ac9fc/file/desiredFilename?refPath=57ba40039d789851ef6ac9fb/",
  "mimeType": "image/png",
  "md5": null
}
```

### Create content's file by uploading a string with the file data

- **HTTP_METHOD**: POST

- **URL**: https://localhost:8443/learningObjects/{loId}/contents/{contentsId}/file

- **Request data:**

JSON with the following structure:

```json
{
  "filename": "The desired file's name. (String)",
  "primaryType": "File's primary type, example: image. (String)",
  "secondaryType": "File's secondary type, example: png. (String)",
  "content": "File text content. (String)",
  "refPath": "Path where the file will be stored in the filesystem. (String)"
}
```

- **Example Query:**

```bash
curl -k --request POST \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57f7fe062d801cd2487161a6/contents/57f7fe6c2d801cd2487161a7/file" \
--data '{
        "filename": "desiredFilename",
        "primaryType": "text",
        "secondaryType": "html",
        "content": "<html><head></head><body></body></html>",
        "refPath": "57f7fe062d801cd2487161a6/"
       }'
```

- **Example response (FileResponse object):**

```json
{
  "url": "/learningObjects/57ba40039d789851ef6ac9fb/contents/57ba414a9d789851ef6ac9fc/file/desiredFilename?refPath=57ba40039d789851ef6ac9fb/",
  "mimeType": "text/json",
  "md5": null
}
```

### Create learning object's content with its file (Base 64)

- **HTTP_METHOD**: POST

- **URL**: https://localhost:8443/learningObjects/{loId}/file

- **Request data:**

JSON object with the following structure:

```json
{
  "filename": "",
  "md5": "",
  "mimeType": "",
  "base64Content": ""
}
```

- **Example Query:**

```bash
curl -k --request POST \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57ba40039d789851ef6ac9fb/file" \
--data '{
        "filename": "filename.xml",
        "md5": "5d39af9a571b3166fe88aad88bd043bc",
        "mimeType": "text/xml",
        "base64Content": "PHF1ZXN0ZXN0aW50ZXJvcCB4bWxucz0na+"
       }'
```

- **Example response (FileResponse object):**

```json
{
  "url": "/learningObjects/57ba40039d789851ef6ac9fb/contents/57ba414a9d789851ef6ac9fc/file/filename.xml?refPath=57ba40039d789851ef6ac9fb/",
  "mimeType": "text/xml",
  "md5": null
}
```

---

### Retrieve a specific learning object by id

- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/learningObjects/{id}

- **Request data:** None.

- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57ba40039d789851ef6ac9fb"
```

- **Example Response:**

```json
{
  "id": "57ba40039d789851ef6ac9fb",
  "creationDate": 1471823875451,
  "modificationDate": 1471831799512,
  "name": "Learning Object name",
  "compoundContent": false,
  "subject": "Learning Object subject",
  "description": "Learning Object description",
  "title": "Learning Object title",
  "type": "EXERCISE",
  "format": "IMAGE",
  "metadata": {
    "keywords": null,
    "coverage": "Learning Object coverage",
    "context": "PRIMARY_EDUCATION",
    "difficulty": "VERY_LOW",
    "endUser": "LEARNERS",
    "interactivityDegree": null,
    "language": "English",
    "status": "FINAL",
    "author": "Learning Object author",
    "topic": null,
    "isbn": "Learning Object ISBN",
    "price": 100,
    "extraMetadata": [
      "Additional metadata key,Additional metadata value"
    ]
  },
  "contents": {
    "id": "57ba414a9d789851ef6ac9fc",
    "creationDate": 1471824202255,
    "modificationDate": 1471831799510,
    "mimeType": "text/xml",
    "md5": null,
    "url": "/learningObjects/57ba40039d789851ef6ac9fb/contents/57ba414a9d789851ef6ac9fc/file/filename.xml?refPath=57ba40039d789851ef6ac9fb/",
    "resourcesURL": [],
    "new": false
  },
  "enabled": true,
  "learningObjectiveList": [],
  "new": false
}

```

---

### Retrieve multiple learning objects

- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/learningObjects

- **Query Params:**

  - **from:** Initial index for the results. (Positive integer, 0-index)
  - **size:** How many learning object you wanna get from the starting index. (Positive integer)
  - **all:** If this option is set to true, from and size attributes are ignored and the response will contain all learning objects in the database. (true/false)

- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects?from=0&size=2&all=false"
```

- **Example Response:**

```json
{
  "content": [
    {
      "id": "57ba40039d789851ef6ac9fb",
      "creationDate": 1471823875451,
      "modificationDate": 1471831799512,
      "name": "Learning Object name",
      "compoundContent": false,
      "subject": "Learning Object subject",
      "description": "Learning Object description",
      "title": "Learning Object title",
      "type": "EXERCISE",
      "format": "IMAGE",
      "metadata": {
        "keywords": null,
        "coverage": "Learning Object coverage",
        "context": "PRIMARY_EDUCATION",
        "difficulty": "VERY_LOW",
        "endUser": "LEARNERS",
        "interactivityDegree": null,
        "language": "English",
        "status": "FINAL",
        "author": "Learning Object author",
        "topic": null,
        "isbn": "Learning Object ISBN",
        "price": 100,
        "extraMetadata": [
          "Additional metadata key,Additional metadata value"
        ]
      },
      "contents": {
        "id": "57ba414a9d789851ef6ac9fc",
        "creationDate": 1471824202255,
        "modificationDate": 1471831799510,
        "mimeType": "text/xml",
        "md5": null,
        "url": "/learningObjects/57ba40039d789851ef6ac9fb/contents/57ba414a9d789851ef6ac9fc/file/filename.xml?refPath=57ba40039d789851ef6ac9fb/",
        "resourcesURL": [],
        "new": false
      },
      "enabled": true,
      "learningObjectiveList": [],
      "new": false
    },
    {
      "id": "57ba193c9d789851ef6ac9f7",
      "creationDate": 1471813948807,
      "modificationDate": 1471813949226,
      "name": "Student_Lab_Evaluation_assessment",
      "compoundContent": false,
      "subject": null,
      "description": "<p><span style=\"font-size: larger;\">Click on the link at the top of the Course Info page to open the evaluation. Please respond thoughtfully and honestly in each of your responses.</span></p>",
      "title": "Student Lab Evaluation",
      "type": "EXAM",
      "format": "XML",
      "metadata": {
        "keywords": null,
        "coverage": null,
        "context": "ANY",
        "difficulty": "ANY",
        "endUser": "ANY",
        "interactivityDegree": "ANY",
        "language": "English",
        "status": "ANY",
        "author": null,
        "topic": null,
        "isbn": null,
        "price": 0,
        "extraMetadata": []
      },
      "contents": {
        "id": "57ba193c9d789851ef6ac9f8",
        "creationDate": 1471813948926,
        "modificationDate": 1471813949220,
        "mimeType": "text/xml",
        "md5": null,
        "url": "/learningObjects/57ba193c9d789851ef6ac9f7/contents/57ba193c9d789851ef6ac9f8/file/Student_Lab_Evaluation_qtiassessment.xml?refPath=57ba193c9d789851ef6ac9f7/",
        "resourcesURL": [],
        "new": false
      },
      "enabled": true,
      "learningObjectiveList": [],
      "new": false
    }
  ],
  "totalPages": 45,
  "totalElements": 90,
  "firstPage": true,
  "lastPage": false,
  "last": false,
  "numberOfElements": 2,
  "sort": [
    {
      "direction": "DESC",
      "property": "modificationDate",
      "ignoreCase": false,
      "nullHandling": "NATIVE",
      "ascending": false
    }
  ],
  "first": true,
  "size": 2,
  "number": 0
}
```

---

### Retrieve learning object's content by id

- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/learningObjects/{loId}/contents/{contentsId}

- **Request data:** None.

- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57ba40039d789851ef6ac9fb/contents/57ba193c9d789851ef6ac9f8"
```

- **Example Response:**

```json
{
  "id": "57ba193c9d789851ef6ac9f8",
  "creationDate": 1471813948926,
  "modificationDate": 1471813949220,
  "mimeType": "text/xml",
  "md5": null,
  "url": "/learningObjects/57ba193c9d789851ef6ac9f7/contents/57ba193c9d789851ef6ac9f8/file/Student_Lab_Evaluation_qtiassessment.xml?refPath=57ba193c9d789851ef6ac9f7/",
  "resourcesURL": [],
  "new": false
}

```

---

### Retrieve the contents associated whit a specific learning object

- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/learningObjects/{loId}/contents

- **Request data:** None.

- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57ba40039d789851ef6ac9fb/contents"
```

- **Example Response:**

```json
{
  "id": "57ba414a9d789851ef6ac9fc",
  "creationDate": 1471824202255,
  "modificationDate": 1471831799510,
  "mimeType": "text/xml",
  "md5": null,
  "url": "/learningObjects/57ba40039d789851ef6ac9fb/contents/57ba414a9d789851ef6ac9fc/file/filename.xml?refPath=57ba40039d789851ef6ac9fb/",
  "resourcesURL": [],
  "new": false
}
```

---

### Retrieve the current file of a learning object's content by id

- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/learningObjects/{loId}/contents/{contentsId}/file/{fileId}

- **Query Params**:

  - refPath (Folder where the file will be saved, using the loId is a good practice. Example: ?refPath=57ba0f7f9d788470efe91b24/). It must end with '/'.

- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57ba0f7f9d788470efe91b24/contents/57ba0f7f9d788470efe91b25/file/My_Profile.html?refPath=57ba0f7f9d788470efe91b24/"
```

- **Example Response:**

The file itself
```html
<p>Go to the "My Profile" tab of the system, and carefully review your profile information.&nbsp; </p>
```

---

### Retrieve file's input stream by Id

- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/learningObjects/{loId}/contents/{contentsId}/file/{fileId}/inputStream

- **Query Params**:

  - refPath (Folder where the file will be saved, using the loId is a good practice. Example: ?refPath=57ba0f7f9d788470efe91b24/). It must end with '/'.

- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57e19c35185e1e8c93db6f61/contents/57e19c35185e1e8c93db6f62/file/textandmaterials.html/inputStream?refPath=57e19c35185e1e8c93db6f61/"
```

- **Example Response:**

A String with the file content
```html
<html>
  <head>
    <title>Texts and Materials</title>
  </head>
  <body>
  </body>
</html>
```


---

### Retrieve file's base64 by Id

- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/learningObjects/{loId}/contents/{contentsId}/file/{fileId}/base64

- **Query Params**:

  - refPath (Folder where the file will be saved, using the loId is a good practice. Example: ?refPath=57ba0f7f9d788470efe91b24/). It must end with '/'.

- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57e19c35185e1e8c93db6f61/contents/57e19c35185e1e8c93db6f62/file/textandmaterials.html/base64?refPath=57e19c35185e1e8c93db6f61/"
```

- **Example Response:**

The following JSON:
```JSON
{
    "base64": "PGh0bWw+CiAgPGhlYWQ+CiAgICA8dGl0bGU+VGV4dHMgYW5kIE1"
}
```


---

### Retrieve a list of all the versions of a specific file

- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/learningObjects/{loId}/public/contents/{contentsId}/file/{fileId}/versions

- **Query Params**:

  - refPath

- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57ba19319d789851ef6ac9e9/contents/57ba19329d789851ef6ac9ea/file/Part_I_-_Special_Topics_in_Earth_Science.html/versions?refPath=57ba19319d789851ef6ac9e9/"
```

- **Example Response:**

```javascript
[{"version":"v1","date":"Sun Aug 21 15:12:19 CST 2016"},{"version":"v2","date":"Sun Aug 21 21:14:55 CST 2016"}]
```

---

### Retrieve a file version by id

- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/learningObjects/{loId}/contents/{contentsId}/file/{fileId}/versions/{version}

- **Query Params**:

  - refPath (Folder where the file will be saved, using the loId is a good practice. Example: ?refPath=57ba0f7f9d788470efe91b24/). It must end with '/'.

- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57e19c35185e1e8c93db6f61/contents/57e19c35185e1e8c93db6f62/file/textandmaterials.html/versions/1?refPath=57e19c35185e1e8c93db6f61/"
```

- **Example Response:**

The file itself
```html
<html>
  <head>
    <title>Texts and Materials</title>
  </head>
  <body>
  </body>
</html>
```

---

### Rollback file to specific version

After a rollback, if you make a request to obtain the current file, it will return the rollbacked version.

- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/learningObjects/{loId}/contents/{contentsId}/file/version/rollback/{version}

- **Query Params**:

  - refPath

- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57ba19319d789851ef6ac9e9/contents/57ba19329d789851ef6ac9ea/file/version/rollback/2?refPath=57ba19319d789851ef6ac9e9/"
```

- **Example Response (FileResponse object):**

```json
{
  "url": "/learningObjects/57ba19319d789851ef6ac9e9/contents/57ba19329d789851ef6ac9ea/file/Part_I_-_Special_Topics_in_Earth_Science.html?refPath=57ba19319d789851ef6ac9e9/",
  "mimeType": "text/HTML",
  "md5": null
}
```

---

### Retrieve all learning objects that contains a specific Learning Objective

- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/linkedlearningobjects

- **Query Params:**

  - **name:** Learning objective's name.
  - **from:** Initial index for the results. (Positive integer, 0-index)
  - **size:** How many learning objectives you wanna get from the starting index. (Positive integer)
  - **all:** If this option is set to true, from and size attributes are ignored and the response will contain all matched learning objects in the database. (true/false)

- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/linkedlearningobjects?from=0&size=2&all=false&name=PSL111-03"
```

- **Example Response:**

```javascript
[
  {
    "id": "57ba40039d789851ef6ac9fb",
    "creationDate": 1471823875451,
    "modificationDate": 1471831799512,
    "name": "Learning Object name",
    "compoundContent": false,
    "subject": "Learning Object subject",
    "description": "Learning Object description",
    "title": "Learning Object title",
    "type": "EXERCISE",
    "format": "IMAGE",
    "metadata": {
      "keywords": null,
      "coverage": "Learning Object coverage",
      "context": "PRIMARY_EDUCATION",
      "difficulty": "VERY_LOW",
      "endUser": "LEARNERS",
      "interactivityDegree": null,
      "language": "English",
      "status": "FINAL",
      "author": "Learning Object author",
      "topic": null,
      "isbn": "Learning Object ISBN",
      "price": 100,
      "extraMetadata": [
        "Additional metadata key,Additional metadata value"
      ]
    },
    "contents": {
      "id": "57ba414a9d789851ef6ac9fc",
      "creationDate": 1471824202255,
      "modificationDate": 1471831799510,
      "mimeType": "text/xml",
      "md5": null,
      "url": "/learningObjects/57ba40039d789851ef6ac9fb/contents/57ba414a9d789851ef6ac9fc/file/filename.xml?refPath=57ba40039d789851ef6ac9fb/",
      "resourcesURL": [],
      "new": false
    },
    "enabled": true,
    "learningObjectiveList": [
      {
        "id": "57ba0f779d788470efe91b13",
        "creationDate": 1471811447274,
        "modificationDate": 1471811447274,
        "name": "ENG132-01",
        "description": "Analyze and synthesize texts and think critically to produce advanced academic discourse.",
        "learningObjectiveList": [],
        "new": false
      }
    ],
    "new": false
  }
]
```

---

### Update learning object

- **HTTP_METHOD**: PUT

- **URL**: https://localhost:8443/learningObjects/{id}

- **Request data:**

  - Learning object JSON.

- **Example Query:**

```bash
curl -k --request PUT \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57ba40039d789851ef6ac9fb" \
--data '{
        "name": "New Learning Object name",
        "subject": "New Learning Object subject",
        "description": "New Learning Object description",
        "title": "New Learning Object title",
        "type": "EXERCISE",
        "format": "IMAGE",
        "metadata": {
          "keywords": "digitalize",
          "coverage": "Learning Object coverage",
          "context": "PRIMARY_EDUCATION",
          "difficulty": "VERY_LOW",
          "endUser": "LEARNERS",
          "interactivityDegree": null,
          "language": "English",
          "status": "FINAL",
          "author": "Learning Object author",
          "topic": null,
          "isbn": "Learning Object ISBN",
          "price": 100,
          "extraMetadata": [
            "Additional metadata key,Additional metadata value"
          ]
        },
        "learningObjectiveList": []
       }'
```

- **Example Response:**

```json
{
  "id": "57ba40039d789851ef6ac9fb",
  "creationDate": 1471823875451,
  "modificationDate": 1471839027128,
  "name": "New Learning Object name",
  "compoundContent": false,
  "subject": "New Learning Object subject",
  "description": "New Learning Object description",
  "title": "New Learning Object title",
  "type": "EXERCISE",
  "format": "IMAGE",
  "metadata": {
    "keywords": "digitalize",
    "coverage": "Learning Object coverage",
    "context": "PRIMARY_EDUCATION",
    "difficulty": "VERY_LOW",
    "endUser": "LEARNERS",
    "interactivityDegree": null,
    "language": "English",
    "status": "FINAL",
    "author": "Learning Object author",
    "topic": null,
    "isbn": "Learning Object ISBN",
    "price": 100,
    "extraMetadata": [
      "Additional metadata key,Additional metadata value"
    ]
  },
  "contents": null,
  "enabled": false,
  "learningObjectiveList": [],
  "new": false
}
```

---

### Update learning object's content

- **HTTP_METHOD**: PUT

- **URL**: https://localhost:8443/learningObjects/{loId}/contents/{contentsId}

- **Request data:**

  - Learning object JSON.

- **Example Query:**

```bash
curl -k --request PUT \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57ba40039d789851ef6ac9fb/contents/57ba414a9d789851ef6ac9fc" \
--data '{
        "mime_type": "text/html",
        "url": "/learningObjects/57ba0f789d788470efe91b1c/contents/57ba0f799d788470efe91b1d/file/textandmaterials.html?refPath=57ba0f789d788470efe91b1c/",
        "md5": "sdhf7$gska7623a&"
       }'
```

- **Example Response:**

```json
{
  "id": "57ba414a9d789851ef6ac9fc",
  "creationDate": 1471824202255,
  "modificationDate": 1471839785687,
  "mimeType": null,
  "md5": "sdhf7$gska7623a&",
  "url": "/learningObjects/57ba0f789d788470efe91b1c/contents/57ba0f799d788470efe91b1d/file/textandmaterials.html?refPath=57ba0f789d788470efe91b1c/",
  "resourcesURL": [],
  "new": false
}
```

---

### Delete learning object and its associated contents

- **HTTP_METHOD**: DELETE

- **URL**: https://localhost:8443/learningObjects/{loId}

- **Request data:** None.

- **Example Query:**

```bash
curl -k --request DELETE \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57ba40039d789851ef6ac9fb"
```

- **Example Response:**

```json
{
  "id": "57ba40039d789851ef6ac9fb",
  "creationDate": 1471823875451,
  "modificationDate": 1471839785692,
  "name": "New Learning Object name",
  "compoundContent": false,
  "subject": "New Learning Object subject",
  "description": "New Learning Object description",
  "title": "New Learning Object title",
  "type": "EXERCISE",
  "format": "IMAGE",
  "metadata": {
    "keywords": "digitalize",
    "coverage": "Learning Object coverage",
    "context": "PRIMARY_EDUCATION",
    "difficulty": "VERY_LOW",
    "endUser": "LEARNERS",
    "interactivityDegree": null,
    "language": "English",
    "status": "FINAL",
    "author": "Learning Object author",
    "topic": null,
    "isbn": "Learning Object ISBN",
    "price": 100,
    "extraMetadata": [
      "Additional metadata key,Additional metadata value"
    ]
  },
  "contents": null,
  "enabled": false,
  "learningObjectiveList": [],
  "new": false
}
```

---

### Delete learning object keeping the associated content

- **HTTP_METHOD**: DELETE

- **URL**: https://localhost:8443/learningObjects/delete/soft/{loId}

- **Request data:** None.

- **Example Query:**

```bash
curl -k --request DELETE \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/delete/soft/57ba0f7c9d788470efe91b20"
```

- **Example Response:**

```json
{
  "id": "57ba0f7c9d788470efe91b20",
  "creationDate": 1471811452303,
  "modificationDate": 1471811453359,
  "name": "Letter_to_the_Students",
  "compoundContent": false,
  "subject": null,
  "description": null,
  "title": "Letter to the Students",
  "type": null,
  "format": "HTML",
  "metadata": {
    "keywords": null,
    "coverage": null,
    "context": "ANY",
    "difficulty": "ANY",
    "endUser": "ANY",
    "interactivityDegree": "ANY",
    "language": "English",
    "status": "ANY",
    "author": null,
    "topic": null,
    "isbn": null,
    "price": 0,
    "extraMetadata": [
      "priority,1",
      "type,READING"
    ]
  },
  "contents": null,
  "enabled": true,
  "learningObjectiveList": [],
  "new": false
}
```

---

### Delete learning object's contents by id

- **HTTP_METHOD**: DELETE

- **URL**: https://localhost:8443/learningObjects/{loId}/contents/{contentsId}

- **Request data:** None.

- **Example Query:**

```bash
curl -k --request DELETE \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "content-type: application/json" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/learningObjects/57ba0f8c9d788470efe91b3b/contents/57ba0f8c9d788470efe91b3c"
```

- **Example Response:**

```json
{
  "id": "57ba0f8c9d788470efe91b3c",
  "creationDate": 1471811468558,
  "modificationDate": 1471811468853,
  "mimeType": "text/html",
  "md5": null,
  "url": "/learningObjects/57ba0f8c9d788470efe91b3b/contents/57ba0f8c9d788470efe91b3c/file/Revising_and_Editing.html?refPath=57ba0f8c9d788470efe91b3b/",
  "resourcesURL": [],
  "new": false
}
```

---

# **Search REST API Reference**

Common Library allows you to perform searches over the stored Learning Objects. The next section will explain the available search endpoints.

### General search

If you want to find Learning Objects that match a specific query string, you must use this endpoint. This operation will search in every Learning Object field (including its associated file).

- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/api/v1/search

- **Query Params:**

  - **from:** Initial index for the results. (Positive integer, 0-index)
  - **size:** How many items you wanna get from the starting index. (Positive integer)
  - **entityType:** Currently the only supported type is: 'LearningObject'. This parameter is optional.
  - **inclusions:** Comma separated terms (strings) that you want to include in the query.
  - **exclusions:** Comma separated terms (strings) that you want to exclude. Items containing any of those terms will be filtered out.
  - **query**: Whitespace separated terms (strings) indicating the user information needs. You can narrow the result set by adding additional filters to this query using the following syntax: +filterName1:filterValue1+filterName2:filterValue2. Make sure that the filters are placed at the end of the query. 


- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/search?query=wikipedia+format:URL&inclusions=wiki&exclusions=HTML,IMAGE&from=0&size=2&entityType=LearningObject"
```

- **Example Response:**

```json
[
  {
    "id": "integrationtestslo000002",
    "creationDate": null,
    "modificationDate": null,
    "name": "Learning Object 2 - Integration Test",
    "compoundContent": false,
    "subject": "Learning Object 2 subject",
    "description": "Learning Object 2 description",
    "title": "Learning Object 2 title",
    "type": "EXERCISE",
    "format": "IMAGE",
    "metadata": {},
    "contents": null,
    "enabled": true,
    "externalUrl": null,
    "learningObjectiveList": [
      {
        "id": "integrationtestsloi00001",
        "creationDate": null,
        "modificationDate": null,
        "name": "Learning Objective 1 - Integration Test",
        "description": "Learning Objective 1 - Integration Test Description",
        "learningObjectiveList": [],
        "new": false
      }
    ],
    "new": false
  },
  {
    "id": "integrationtestslo000001",
    "creationDate": null,
    "modificationDate": null,
    "name": "Learning Object 1 - Integration Test",
    "compoundContent": false,
    "subject": "Learning Object 1 subject",
    "description": "Learning Object 1 description",
    "title": "Learning Object 1 title",
    "type": "EXERCISE",
    "format": "IMAGE",
    "metadata": {},
    "contents": null,
    "enabled": true,
    "externalUrl": null,
    "learningObjectiveList": [],
    "new": false
  }
]
```

---

### Find alternative terms.

You can obtain alternative terms by providing a query string. Common Library will return a list containing one suggestion per term. If no suggestions found, an empty list will be returned.

- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/api/v1/search/altTerms

- **Query Params:**

  - **query**: Whitespace separated terms (strings) indicating the user information needs. 


- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/search/altTerms?query=wikimedia%20chapios"
```

- **Example Response:**

```json
["wikipedia","champions"]
```

---

### MoreLikeThis Search

This endpoint allows you to find objects that are like another object, you just need to provide the object's id.


- **HTTP_METHOD**: GET

- **URL**: https://localhost:8443/api/v1/search/moreLikeThis/{id}

- **Query Params:**

  - **from:** Initial index for the results. (Positive integer, 0-index)
  - **size:** How many items you wanna get from the starting index. (Positive integer)
  - **entityType:** Currently the only supported type is: 'LearningObject'.


- **Example Query:**

```bash
curl -k --request GET \
--header "Authorization: SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160905/8hcW7pqAMx/sauthc1_request, sauthc1SignedHeaders=host;x-stormpath-date, sauthc1Signature=1e84666356c28a6b029f899ef3969876292672f44abeee6f068d978420497b1e" \
--header "X-Stormpath-Date: 20160905T153936Z" \
--url "https://localhost:8443/api/v1/search/moreLikeThis/integrationtestslo000001?from=0&size=1&entityType=LearningObject"
```

- **Example Response:**

```json
[
  {
    "id": "integrationtestslo000002",
    "creationDate": null,
    "modificationDate": null,
    "name": "Learning Object 2 - Integration Test",
    "compoundContent": false,
    "subject": "Learning Object 2 subject",
    "description": "Learning Object 2 description",
    "title": "Learning Object 2 title",
    "type": "EXERCISE",
    "format": "IMAGE",
    "metadata": {},
    "contents": null,
    "enabled": true,
    "externalUrl": null,
    "learningObjectiveList": [
      {
        "id": "integrationtestsloi00001",
        "creationDate": null,
        "modificationDate": null,
        "name": "Learning Objective 1 - Integration Test",
        "description": "Learning Objective 1 - Integration Test Description",
        "learningObjectiveList": [],
        "new": false
      }
    ],
    "new": false
  }
]
```
