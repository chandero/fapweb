<template >
  <el-container>
    <el-header>
      <el-row>
        <el-col :span="24">
          <h1>Informe de Recaudos por Caja y Bancos del Mes</h1>
        </el-col>
      </el-row>
    </el-header>
    <el-main>
      <el-form v-model="form">
        <el-row :gutter="4">
          <el-col :span="3">
            <el-form-item label="Año">
              <el-input v-model="form.anho" type="number" />
            </el-form-item>
          </el-col>
          <el-col :span="2">
            <el-form-item label="Mes">
              <el-input v-model="form.mes" type="number" />
            </el-form-item>
          </el-col>
          <el-col>
            <el-button :disabled="form.anho == null || form.mes == null" type="success" icon="el-icon-search" title="Buscar Datos" @click="buscar">Buscar</el-button>
          </el-col>
        </el-row>
      </el-form>
      <el-row>
        <el-col :span="24">
          <el-table v-loading="loading" :data="tableData" stripe max-height="400px" style="font-size:12px;">
            <el-table-column sortable label="Día" prop="dia" width="80">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.dia }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Código" prop="dia" width="180">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.codigo }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Cuenta" prop="dia" width="300">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.cuenta }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Recaudo" prop="valor" width="150" align="right">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.recaudo | toThousandFilter(2) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-button :loading="downloadLoading" style="margin-bottom:20px;" type="primary" icon="document" @click="handleDownload" >{{ $t('excel.export') }} excel</el-button>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import { parseTime } from '@/utils'
import { recaudoDiarioMes } from '@/api/informe'

export default {
  data() {
    return {
      form: {
        anho: null,
        mes: null
      },
      tableData: [],
      loading: false,
      autoWidth: true,
      bookType: 'xlsx',
      downloadLoading: false,
      filename: ''
    }
  },
  beforeMount() {
    const _fecha = new Date()
    this.form.anho = _fecha.getFullYear()
    this.form.mes = _fecha.getMonth() + 1
  },
  methods: {
    buscar() {
      this.loading = true
      recaudoDiarioMes(this.form.anho, this.form.mes).then(response => {
        this.loading = false
        this.tableData = response.data
      }).catch(() => {
        this.loading = false
      })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['Día', 'Código', 'Cuenta', 'Recaudo']
        const filterVal = ['dia', 'codigo', 'cuenta', 'recaudo']
        const list = this.tableData
        const data = this.formatJson(filterVal, list)
        this.filename = 'RecuadoCajaBancos_' + this.form.anho + '_' + this.form.mes
        excel.export_json_to_excel({
          header: tHeader,
          data,
          filename: this.filename,
          autoWidth: this.autoWidth,
          bookType: this.bookType
        })
        this.downloadLoading = false
      })
    },
    formatJson(filterVal, jsonData) {
      return jsonData.map(v => filterVal.map(j => {
        if (j === 'timestamp') {
          return parseTime(v[j])
        } else {
          return v[j]
        }
      }))
    }
  }
}
</script>
