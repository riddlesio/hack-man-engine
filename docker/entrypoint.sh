#!/bin/bash

# If user exists, change user and group ids, otherwise add new local user
# Either use the LOCAL_USER_ID if passed in at runtime or
# fallback

USER_NAME=${USERNAME:-gradle}
USER_ID=${LOCAL_USER_ID:-1000}
GROUP_ID=${LOCAL_GROUP_ID:-1000}

if id -u $USER_NAME > /dev/null 2>&1; then
    echo "Starting with UID: $USER_ID and GID: $GROUP_ID"
    # modify existing gradle user instead of creating new one
    usermod -o --uid $USER_ID $USER_NAME
    groupmod -o --gid $GROUP_ID $USER_NAME
else
    echo "Adding new user $USER_NAME with UID: $USER_ID and GID: $GROUP_ID"
    useradd --shell /bin/bash -u $USER_ID -o -c "" -m $USER_NAME
fi

export HOME=/home/$USER_NAME

# Set Appropriate Environmental Variables
mkdir -p /gradle-app/.gradle
chown $USER_NAME /gradle-app/.gradle
export GRADLE_HOME="/gradle-app/.gradle"
export PATH=$PATH:$GRADLE_HOME/bin

exec /usr/local/bin/gosu $USER_NAME "$@"
