<template >
  <el-container>
    <el-header>
      <el-row>
        <el-col :span="24">
          <h1>Informe de Recaudo Interés Causado con Periodo de Gracia</h1>
        </el-col>
      </el-row>
    </el-header>
    <el-main>
      <el-form v-model="form">
        <el-row :gutter="4">
          <el-col :span="5">
            <el-form-item label="Fecha Inicial">
              <el-date-picker v-model="form.fecha_inicial" format="yyyy-MM-dd" style="width: 80%;" />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item label="Fecha Final">
              <el-date-picker :picker-options="datePickerOptions" v-model="form.fecha_final" format="yyyy-MM-dd" style="width: 80%;" />
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
            <el-table-column sortable label="Colocación" prop="id_colocacion" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.id_colocacion }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Documento" prop="id_persona" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.id_persona }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Nombre" prop="nombre" width="300">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.nombre | fm_truncate(50) }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Fecha Recaudo" prop="valor" width="140" >
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_recaudo | moment("YYYY-MM-DD") }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Código" prop="codigo" width="160">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.codigo }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Cuenta" prop="cuenta" width="300">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.cuenta | fm_truncate(30) }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Fecha Inicial" prop="valor" width="120" >
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_inicial | moment("YYYY-MM-DD") }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Fecha Final" prop="valor" width="120" >
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_final | moment("YYYY-MM-DD") }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Interés" prop="interes" width="150" align="right">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.interes | toThousandFilter(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Días" prop="dias" width="100" >
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.dias }}</span>
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
import { parseDate } from '@/utils'
import { recaudoInteresCausadoPeriodoGracia } from '@/api/informe'

export default {
  data() {
    return {
      form: {
        fecha_inicial: new Date(),
        fecha_final: new Date()
      },
      tableData: [],
      loading: false,
      autoWidth: true,
      bookType: 'xlsx',
      downloadLoading: false,
      filename: '',
      datePickerOptions: {
        disabledDate: this.validarFecha
      }
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
      recaudoInteresCausadoPeriodoGracia(this.form.fecha_inicial.getTime(), this.form.fecha_final.getTime()).then(response => {
        this.loading = false
        this.tableData = response.data
      }).catch(() => {
        this.loading = false
      })
    },
    validarFecha(date) {
      const repo_fecha = new Date(this.form.fecha_inicial)
      const recepcion = new Date(repo_fecha.getFullYear(), repo_fecha.getMonth(), repo_fecha.getDate())
      const result1 = (date.getTime() >= new Date(recepcion).getTime())
      const result2 = (date.getTime() >= new Date(recepcion).getTime())
      const result = result1 && result2
      return !result
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['Colocación', 'Documento', 'Nombre', 'Fecha Recaudo', 'Código', 'Cuenta', 'Fecha Inicial', 'Fecha Final', 'Interés', 'Días']
        const filterVal = ['id_colocacion', 'id_persona', 'nombre', 'fecha_recaudo', 'codigo', 'cuenta', 'fecha_inicial', 'fecha_final', 'interes', 'dias']
        const list = this.tableData
        const data = this.formatJson(filterVal, list)
        this.filename = 'RecuadoInteresCausadoPeriodoGracia_' + parseDate(this.form.fecha_inicial) + '_' + parseDate(this.form.fecha_final)
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
        if (j.includes('fecha', 0)) {
          return parseDate(v[j])
        } else {
          return v[j]
        }
      }))
    }
  }
}
</script>
