/**
 * Main verticle for the office events application. This verticle 
 * installs the modules and the worker verticle that make the 
 * application do its job.
 *
 * @author Jettro Coenradie
 */



def log = container.logger

def destiationPersist = "vertx.persist"
def topicAllClients = "message.all.clients"

def appConfig = [
    persisterConfig: [
        "db_name": "officeevents", 
        "address": destiationPersist
    ],
    websiteConfig: [
        "port": 8080
    ],
    notificationsConfig: [
        "all_clients_address": topicAllClients,
        "notification_received": "notification.received"
    ],
    invitationConfig: [
        "all_clients_address": topicAllClients,
        "invitation_new": "message.newinvitation",
        "invitation_register": "message.registerinvitation",
        "vertx_persist": destiationPersist
    ],
    backupConfig: [
        "backup_create":"message.backup.create",
        "vertx_persist": destiationPersist
    ]
]

container.with {
    deployModule('vertx.mongo-persistor-v1.2', appConfig["persisterConfig"], 1) {
        log.info "App: Started the mongo-persister module."
    }
    deployModule('website', appConfig["websiteConfig"], 2) {
        log.info "App: Started the website module."
    }
    deployModule('notifications', appConfig["notificationsConfig"],1) {
        log.info "App: Started the notifications module."
    }
    deployModule('invitations', appConfig["invitationConfig"],1) {
        log.info "App: Started the invitations module."
    }
    deployWorkerVerticle('app/backup/Backup.groovy', appConfig["backupConfig"],1) {
        log.info "App: Started the backup worker verticle."
    }
}

