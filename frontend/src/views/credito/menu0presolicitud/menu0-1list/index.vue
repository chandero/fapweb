<template >
    <el-container>
      <el-header>
        <el-row>
          <el-col :span="24">
            <h1>Informe de Presolicitudes</h1>
          </el-col>
        </el-row>
      </el-header>
      <el-main>
<!--         <el-form v-model="form">
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
-->     
        <el-row>
            <el-col>
                <el-button type="primary" icon="el-icon-refresh" title="Cargar Datos" @click="buscar">Cargar</el-button>
            </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-table v-loading="loading" :data="tableData" stripe max-height="400px" style="font-size:12px;">
              <el-table-column sortable label="Fecha Recepción" prop="created_at" width="100">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.created_at | moment('YYYY-MM-DD') }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable label="Documento" prop="id_persona" width="130">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.id_persona }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable label="Nombre" prop="nombre" width="300">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.primer_nombre + " " + scope.row.segundo_nombre + " " +  scope.row.primer_apellido + " " + scope.row.segundo_apellido | fm_truncate(80) }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable label="Monto Solicitado" prop="monto_solicitado" width="160" >
                <template slot-scope="scope">
                  <span style="margin-left: 10px">${{ scope.row.monto_solicitado | currency_2 }}</span>
                </template>
              </el-table-column>              
              <el-table-column sortable label="Plazo Solicitado (meses)" prop="plazo_solicitado" width="200" >
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.plazo_solicitado }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable label="Evaluación" prop="prso_evaluacion_previa" width="160">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ evaluacion(scope.row.prso_evaluacion_previa) }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable label="Máx Mora" prop="prso_evaluacion_mora" width="120">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.prso_evaluacion_mora }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable label="Teléfono" prop="prso_telefono_contacto" width="120">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.numero_celular }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable label="Email" prop="prso_email" width="200">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.email }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable label="Estado" prop="prso_estado" width="120">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ estado(scope.row.prso_estado) }}</span>
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
  import { obtenerListaPresolicitud } from '@/api/credito'
  
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
      //this.buscar()
    },
    methods: {
      buscar() {
        this.loading = true
        console.log("En Buscar")
        obtenerListaPresolicitud().then(response => {
          console.log("Llego la respuesta")
          this.loading = false
          this.tableData = response.data
        }).catch(() => {
          console.log("Error")
          this.loading = false
        })
      },
      evaluacion(id){
        switch(id) {
            case 1:
                return "Excelente"
            case 2:
                return "Bueno"
            case 3:
                return "Aceptable"
            case 4:
                return "Dudoso"
            default:
                return "Rechazado"
        }
      },
      estado(id){
        switch(id) {
            case 1:
                return "Pendiente"
            case 2:
                return "En Atención"
            case 3:
                return "Aceptada"
            default:
                return "Rechazada"
        }
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
        obtenerListaPresolicitud(this.form.fecha_inicial.getTime(), this.form.fecha_final.getTime()).then(response => {
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
  