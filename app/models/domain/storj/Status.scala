package models.domain.storj

sealed trait Status
case object Active extends Status
case object Inactive extends Status
