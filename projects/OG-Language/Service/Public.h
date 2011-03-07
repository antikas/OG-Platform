/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

#ifndef __inc_og_language_service_public_h
#define __inc_og_language_service_public_h

// Public interface for Service component

#define SERVICE_SETTINGS_CONNECTION_PIPE	TEXT ("connectionPipe")
#ifdef _WIN32
#define SERVICE_DEFAULT_CONNECTION_PIPE		TEXT ("\\\\.\\pipe\\OpenGammaLanguageAPI-Connection")
#else
#define SERVICE_DEFAULT_CONNECTION_PIPE		TEXT ("/var/run/OG-Language/Connection.sock")
#endif
#define SERVICE_SETTINGS_SERVICE_NAME		TEXT ("serviceName")
#define SERVICE_DEFAULT_SERVICE_NAME		TEXT ("OpenGammaLanguageAPI")

#include "ClientConnect.h"

#endif /* ifndef __inc_og_language_service_public_h */