package org.groovyblogs

import grails.plugin.i18nEnums.annotations.I18nEnum


@I18nEnum(shortName = true, postfix = '.label')
public enum UpdateUserStatus {
    NEW_EMAIL,
    PASSWORD_UPDATED,
    BOTH,
    NONE
}