<template >
    <el-container>
      <el-header>
        <el-row>
          <el-col :span="24">
            <h1>Informe de Recaudo Vía Web</h1>
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
              <el-button :disabled="form.fecha_inicial == null || form.fecha_final == null" type="success" icon="el-icon-search" title="Buscar Datos" @click="buscar">Buscar</el-button>
            </el-col>
          </el-row>
        </el-form>
        <el-row>
          <el-col :span="24">
            <el-table v-loading="loading" :data="tableData" stripe max-height="400px" style="font-size:12px;">
              <el-table-column sortable label="Colocación" prop="id_colocacion" width="110">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row._4 }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable label="Documento" prop="id_persona" width="130">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row._7 }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable label="Nombre" prop="nombre" width="300">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row._8 | fm_truncate(50) }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable label="Fecha Liquidación" prop="valor" width="160" >
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row._1 | moment("YYYY-MM-DD HH:mm:ss") }}</span>
                </template>
              </el-table-column>              
              <el-table-column sortable label="Fecha Recaudo" prop="valor" width="160" >
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row._2 | moment("YYYY-MM-DD HH:mm:ss") }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable label="Referencia" prop="referencia" width="270">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row._3 }}</span>
                </template>
              </el-table-column>              
              <el-table-column sortable label="Comprobante" prop="comprobante" width="160">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row._5 }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable label="Valor" prop="cuenta" width="300">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">${{ scope.row._9 | currency_2 }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-button :loading="loading" :disabled="tableData.length < 1?true: false" style="margin-bottom:20px;" type="primary" icon="document" @click="handleExcel">{{ $t('excel.export') }} excel</el-button>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </template>
  <script>
  import { parseDate } from '@/utils'
  import { getLiquidacionAplicadaWeb, getLiquidacionAplicadaWebXlsx } from '@/api/informe'
  
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
        getLiquidacionAplicadaWeb(this.form.fecha_inicial.getTime(), this.form.fecha_final.getTime()).then(response => {
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

      },
      handleExcel() {
        this.loading = true
        getLiquidacionAplicadaWebXlsx(this.form.fecha_inicial.getTime(), this.form.fecha_final.getTime()).then(response => {
          this.loading = false
          const filename = response.headers['content-disposition'].split('=')[1]
          const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
          const link = document.createElement('a')
          link.href = window.URL.createObjectURL(blob)
          link.download = filename
          link.click()
        }).catch(() => {
          this.loading = false
        })        
      },
      formatJson(filterVal, jsonData) {
        return jsonData.map(v => filterVal.map(j => {
          if (j == '_1' || j == '_2') {
            return parseDate(v[j])
          } else {
            return v[j]
          }
        }))
      }
    }
  }
  </script>
  