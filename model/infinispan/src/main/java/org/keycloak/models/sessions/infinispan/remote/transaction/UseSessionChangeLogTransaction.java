/*
 * Copyright 2024 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.keycloak.models.sessions.infinispan.remote.transaction;

import io.reactivex.rxjava3.core.Maybe;
import org.infinispan.client.hotrod.RemoteCache;
import org.keycloak.models.sessions.infinispan.changes.remote.remover.query.UserSessionQueryConditionalRemover;
import org.keycloak.models.sessions.infinispan.changes.remote.updater.UpdaterFactory;
import org.keycloak.models.sessions.infinispan.changes.remote.updater.user.UserSessionUpdater;
import org.keycloak.models.sessions.infinispan.entities.RemoteUserSessionEntity;

/**
 * Syntactic sugar for
 * {@code RemoteChangeLogTransaction<SessionKey, UserSessionEntity, UserSessionUpdater,
 * UserAndClientSessionConditionalRemover<UserSessionEntity>>}
 */
public class UseSessionChangeLogTransaction extends RemoteChangeLogTransaction<String, RemoteUserSessionEntity, UserSessionUpdater, UserSessionQueryConditionalRemover> {

    public UseSessionChangeLogTransaction(UpdaterFactory<String, RemoteUserSessionEntity, UserSessionUpdater> factory, RemoteCache<String, RemoteUserSessionEntity> cache) {
        super(factory, cache, new UserSessionQueryConditionalRemover());
    }

    public UserSessionUpdater wrapFromProjection(Object[] projection) {
        assert projection.length == 2;
        RemoteUserSessionEntity entity = (RemoteUserSessionEntity) projection[0];
        return wrap(entity.getUserSessionId(), entity, (long) projection[1]);
    }

    public Maybe<UserSessionUpdater> maybeGet(String userSessionId) {
        return Maybe.fromCompletionStage(getAsync(userSessionId));
    }

}
