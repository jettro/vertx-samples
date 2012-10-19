/**
 * @author Jettro Coenradie
 *
 * export VERTX_MODS=/Users/jcoenradie/sources/vertx-samples/office-events/mods
 *
 *
 */
def log = container.logger

def mongoConfig = ["db_name": "officeevents", "address": "vertx.persist"]

container.with {
    deployModule('vertx.mongo-persistor-v1.1', mongoConfig, 1) { deploymentID ->
        log.info "Started the mongo-persister : $deploymentID"
    }
    deployModule('website', [:], 4) {deploymentID ->
        log.info "Started the website module : $deploymentID"
    }
    deployModule('notifications') { deploymentID ->
        log.info "Started the notifications module : $deploymentID"
    }
    deployModule('invitations') { deploymentID ->
        log.info "Started the invitations module : $deploymentID"
    }
    deployWorkerVerticle('app/backup/Backup.groovy') { deploymentID ->
        log.info "Started the backup module : $deploymentID"
    }

}

