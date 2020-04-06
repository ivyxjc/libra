package com.ivyxjc.libra.core.dao.handler

import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet

class GuidLongHandler : BaseTypeHandler<String>() {
    override fun getNullableResult(rs: ResultSet?, columnName: String?): String? {
        if (rs == null) {
            return null
        }
        val res = rs.getLong(columnName)
        return res.toString()
    }

    override fun getNullableResult(rs: ResultSet?, columnIndex: Int): String? {
        if (rs == null) {
            return null
        }
        val res = rs.getLong(columnIndex)
        return res.toString()
    }

    override fun getNullableResult(cs: CallableStatement?, columnIndex: Int): String? {
        if (cs == null) {
            return null
        }
        val res = cs.getLong(columnIndex)
        return res.toString()
    }

    override fun setNonNullParameter(ps: PreparedStatement?, i: Int, parameter: String?, jdbcType: JdbcType?) {
        ps?.setLong(i, parameter!!.toLong())
    }

}