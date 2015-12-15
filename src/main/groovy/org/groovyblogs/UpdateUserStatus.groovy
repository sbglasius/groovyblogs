package org.groovyblogs

import grails.plugins.i18nenums.annotations.I18nEnum


@I18nEnum(shortName = true, postfix = '.label')
public enum UpdateUserStatus {
    NEW_EMAIL,
    PASSWORD_UPDATED,
    DATA_UPDATED
}