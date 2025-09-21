package com.rio.rostry.domain.rbac

import com.rio.rostry.domain.model.Permission
import com.rio.rostry.domain.model.UserType

object RbacGuard {
    fun can(userType: UserType, permission: Permission): Boolean = Rbac.has(userType, permission)
}
