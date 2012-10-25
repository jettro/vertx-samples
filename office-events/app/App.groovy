/**
 * This verticle installs the modules and the worker verticle that make the application run.
 *
 * @author Jettro Coenradie
 */
def log = container.logger

def mongoConfig = ["db_name": "officeevents", "address": "vertx.persist"]

container.with {
    deployModule('vertx.mongo-persistor-v1.2', mongoConfig, 1) { deploymentID ->
        log.info "Started the mongo-persister module."
    }
    deployModule('website', [:], 4) {deploymentID ->
        log.info "Started the website module."
    }
    deployModule('notifications') { deploymentID ->
        log.info "Started the notifications module."
    }
    deployModule('invitations') { deploymentID ->
        log.info "Started the invitations module."
    }
    deployWorkerVerticle('app/backup/Backup.groovy') { deploymentID ->
        log.info "Started the backup worker verticle."
    }

}

