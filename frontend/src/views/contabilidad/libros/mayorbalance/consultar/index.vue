<template>
  <el-container class="libromayor-container">
    <el-main>
      <el-container>
        <el-header class="libromayor_header">{{ $t('libromayor.tabletitle') }}
        </el-header>
      </el-container>
      <el-container>
        <el-header>
          <el-form>
            <el-row>
              <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
                <el-form-item label="Año">
                  <el-input v-model="anho" type="number" >Año</el-input>
                </el-form-item>
              </el-col>
              <el-col :xs="12" :sm="12" :md="6" :lg="6" :xl="6">
                <el-form-item>
                  <el-button type="success" icon="el-icon-refresh" circle title="Refrescar" @click="consultar" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-header>
        <el-main>
          <el-table
            :data="tableData"
            :default-sort = "{prop: 'lire_anho', order: 'ascending'}"
            stripe
            style="width: 100%"
            max-height="600"
            border>
            <el-table-column
              :label="$t('anho')"
              width="150"
              prop="lire_anho"
              resizable
            />
            <el-table-column
              :label="$t('periodo')"
              width="150"
              prop="lire_periodo"
              resizable
            />
            <el-table-column
              :label="$t('consecutivo')"
              min-width="100"
              prop="lire_consecutivo"
            />
            <el-table-column
              :label="$t('fecha')"
              min-width="150"
              prop="lire_fecha"
            >
              <template slot-scope="scope"><span>{{ scope.row.lire_fecha | moment('YYYY/MM/DD') }}</span>
              </template>
            </el-table-column>
            <el-table-column
              :label="$t('hora')"
              width="150"
              prop="lire_hora"
            >
              <template slot-scope="scope"><span>{{ scope.row.lire_hora | moment('HH:mm:ss') }}</span>
              </template>
            </el-table-column>
            <el-table-column
              :label="$t('accion')"
              fixed="right"
              width="93">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  circle
                  type="warning"
                  title="Acción"
                  @click="handleView(scope.$index, scope.row)"
                ><i class="el-icon-open"/></el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-main>
      </el-container>
    </el-main>
  </el-container>
</template>
<script>
import { conLibroMayor, verLibroMayor } from '@/api/informe'

export default {
  data() {
    return {
      tableData: [],
      page_size: 10,
      current_page: 1,
      total: 0,
      order: '',
      filter: '',
      anho: new Date().getFullYear()
    }
  },
  methods: {
    handleFilter(filters) {
      this.filter = JSON.stringify(filters)
    },
    handleSort({ column, prop, order }) {
      console.log('filter:' + JSON.stringify(this.filter))
    },
    consultar() {
      conLibroMayor(this.anho).then(response => {
        this.tableData = response.data
      })
    },
    handleView(index, row) {
      verLibroMayor(row.lire_anho, row.lire_periodo, row.lire_consecutivo)
    }
  }
}
</script>

<style scoped>
td {
  padding: 4px 0;
}
span.el-pagination__total {
  font-size: 16px;
}
div.match-type-container {
    color: #333;
    background-color: #f5f5f5;
    border-color: #ddd;
}
.vue-query-builder >>> .vqb-group {
    color: #333;
    background-color: #f5f5f5;
    border-color: #ddd;
}
.vue-query-builder >>> .vqb-group.depth-2 {
    border-left: 2px solid #8bc34a;
}

.vue-query-builder >>> .vqb-group.depth-3 {
    border-left: 2px solid #ffb94b;
}

.vue-query-builder >>> .panel {
  margin-bottom: 20px;
  background-color: #fff;
  border: 1px solid transparent;
  border-radius: 4px;
  box-shadow: 0 1px 1px rgba(0,0,0,0.05)
}
.vue-query-builder >>> .panel-default {
  border-color: #ddd;
}

.vue-query-builder >>> .pull-right {
  float: right!important;
}

.vue-query-builder >>> .btn {
    display: inline-block;
    line-height: 1;
    white-space: nowrap;
    cursor: pointer;
    background: #fff;
    border: 1px solid #dcdfe6;
    color: #606266;
    -webkit-appearance: none;
    text-align: center;
    -webkit-box-sizing: border-box;
    box-sizing: border-box;
    outline: 0;
    margin: 0;
    -webkit-transition: .1s;
    transition: .1s;
    font-weight: 500;
    padding: 12px 20px;
    font-size: 14px;
    border-radius: 4px;
}
.vue-query-builder >>> .btn:hover {
    color: #409eff;
    border-color: #c6e2ff;
    background-color: #ecf5ff;
}
</style>
