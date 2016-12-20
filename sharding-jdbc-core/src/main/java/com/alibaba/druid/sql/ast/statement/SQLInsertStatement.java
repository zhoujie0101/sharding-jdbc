/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SQLInsertStatement extends SQLInsertInto implements SQLStatement {
    
    private final List<String> identifiersBetweenInsertAndInto = new ArrayList<>();
    
    private final List<String> identifiersBetweenIntoAndTable = new ArrayList<>();
    
    private final List<String> identifiersBetweenTableAndValues = new ArrayList<>();
    
    @Override
    public String getDbType() {
        return null;
    }
    
    @Override
    protected void acceptInternal(final SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getTableSource());
            acceptChild(visitor, getColumns());
            acceptChild(visitor, getValues());
            acceptChild(visitor, getQuery());
        }
        visitor.endVisit(this);
    }
    
    @Getter
    public static class ValuesClause extends SQLObjectImpl {
        
        private final List<SQLExpr> values;
        
        public ValuesClause(){
            this(new ArrayList<SQLExpr>());
        }
        
        public ValuesClause(final List<SQLExpr> values) {
            this.values = values;
            for (SQLExpr each : values) {
                each.setParent(this);
            }
        }
        
        @Override
        protected void acceptInternal(final SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, values);
            }
            visitor.endVisit(this);
        }
        
        @Override
        public void output(final StringBuffer buffer) {
            buffer.append(" VALUES (");
            for (int i = 0, size = values.size(); i < size; ++i) {
                if (i != 0) {
                    buffer.append(", ");
                }
                values.get(i).output(buffer);
            }
            buffer.append(")");
        }
    }
}