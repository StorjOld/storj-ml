package models.domain.storj

sealed trait Operation
case object Push extends Operation
case object Pull extends Operation