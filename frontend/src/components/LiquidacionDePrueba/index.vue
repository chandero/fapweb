<template>
  <el-container>
    <el-main>
      <el-form v-model="form">
        <el-row :gutter="4">
          <el-col :span="4">
            <span>Identificación</span>
          </el-col>
          <el-col :span="8">
            <span>Nombre</span>
          </el-col>
          <el-col :span="8">
            <span>Colocación</span>
          </el-col>
        </el-row>
        <el-row :gutter="4">
          <el-col :span="4">
            <h5>{{ colocacion.id_persona }}</h5>
          </el-col>
          <el-col :span="8">
            <h5>{{ colocacion.nombre }}</h5>
          </el-col>
          <el-col :span="8">
            <h5>{{ colocacion.id_colocacion }}</h5>
          </el-col>
        </el-row>
        <el-row :gutter="4">
          <el-col :span="4">
            <span>Saldo Actual</span>
          </el-col>
          <el-col :span="3">
            <span>Plazo</span>
          </el-col>
          <el-col :span="3">
            <span>Amort.K</span>
          </el-col>
          <el-col :span="3">
            <span>Amort.I</span>
          </el-col>
          <el-col :span="3">
            <span>Tasa Nominal</span>
          </el-col>
          <el-col :span="4">
            <span>Valor Cuota</span>
          </el-col>
          <el-col :span="2">
            <span>F. Capital</span>
          </el-col>
          <el-col :span="2">
            <span>F. Interés</span>
          </el-col>
        </el-row>
        <el-row :gutter="4">
          <el-col :span="4">
            <h5>${{ colocacion.saldo | toThousandFilter }}</h5>
          </el-col>
          <el-col :span="3">
            <h5>{{ colocacion.plazo }}</h5>
          </el-col>
          <el-col :span="3">
            <h5>{{ colocacion.amortiza_capital }}</h5>
          </el-col>
          <el-col :span="3">
            <h5>{{ colocacion.amortiza_interes }}</h5>
          </el-col>
          <el-col :span="3">
            <h5>{{ colocacion.tasa_nominal }}%</h5>
          </el-col>
          <el-col :span="4">
            <h5>${{ colocacion.cuota | toThousandFilter }}</h5>
          </el-col>
          <el-col :span="2">
            <h5>{{ colocacion.fecha_capital | moment("YYYY-MM-DD") }}</h5>
          </el-col>
          <el-col :span="2">
            <h5>{{ colocacion.fecha_interes | moment("YYYY-MM-DD") }}</h5>
          </el-col>
        </el-row>
        <el-row :gutter="4">
          <el-col :xs="4" :sm="4" :md="4" :lg="4" :xl="4">
            <el-form-item label="Cuotas">
              <el-input-number v-model="form.cuotas" :max="colocacion.numero_cuotas" :min="1" controls-position="right"/>
            </el-form-item>
          </el-col>
          <el-col :xs="2" :sm="2" :md="2" :lg="2" :xl="2">
            <el-form-item :label="form.cuotas.toString()">
              <span>de {{ colocacion.numero_cuotas }}</span>
            </el-form-item>
          </el-col>
          <el-col :xs="4" :sm="4" :md="4" :lg="4" :xl="4">
            <el-form-item label="Fecha Corte">
              <el-date-picker v-model="form.fecha_corte" style="width:100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="1">
            <el-form-item label="_">
              <el-button type="primary" @click="liquidar()">Liquidar</el-button>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <h4>Detalle de la Liquidación</h4>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-table :data="tableData" size="mini" width="100%" style="max-height: 250px; overflow: auto; font-size: 11px;">
              <el-table-column label="Cuot" width="50px;">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.cuotaNumero }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Código" width="150px;">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.codigoPuc }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Cuenta" width="250px;">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.codigoNombre | fm_truncate(32) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="F.Inicial" width="110px;">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.fechaInicial | moment("YYYY-MM-DD") }}</span>
                </template>
              </el-table-column>
              <el-table-column label="F.Final" width="110px;">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.fechaFinal | moment("YYYY-MM-DD") }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Días" width="50px;">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.dias }}</span>
                </template>
              </el-table-column>
              <el-table-column label="%" width="80px;" align="right">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.tasa.toFixed(2) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Débito" width="110px;" align="right">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.debito.toFixed(2) | toThousandFilter }}</span>
                </template>
              </el-table-column>
              <el-table-column label="Crédito" width="110px;" align="right">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.credito.toFixed(2) | toThousandFilter }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
        <el-row :gutter="4">
          <el-col :span="3">
            <h5>Capital</h5>
          </el-col>
          <el-col :span="3">
            <h5>I.Causado</h5>
          </el-col>
          <el-col :span="3">
            <h5>I.Servicio</h5>
          </el-col>
          <el-col :span="3">
            <h5>I.Anticipado</h5>
          </el-col>
          <el-col :span="3">
            <h5>I.Devuelto</h5>
          </el-col>
          <el-col :span="3">
            <h5>I.Mora</h5>
          </el-col>
          <el-col :span="3">
            <h5>Otros Cobros</h5>
          </el-col>
          <el-col :span="3">
            <h5>Total A Pagar</h5>
          </el-col>
        </el-row>
        <el-row :gutter="4">
          <el-col :span="3">
            <span>${{ capital | toThousandFilter }}</span>
          </el-col>
          <el-col :span="3">
            <span>${{ causado | toThousandFilter }}</span>
          </el-col>
          <el-col :span="3">
            <span>${{ servicio | toThousandFilter }}</span>
          </el-col>
          <el-col :span="3">
            <span>${{ anticipado | toThousandFilter }}</span>
          </el-col>
          <el-col :span="3">
            <span>${{ devuelto | toThousandFilter }}</span>
          </el-col>
          <el-col :span="3">
            <span>${{ mora | toThousandFilter }}</span>
          </el-col>
          <el-col :span="3">
            <span>${{ otro | toThousandFilter }}</span>
          </el-col>
          <el-col :span="3">
            <span style="font-weight: bold;">${{ total | toThousandFilter }}</span>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24" align="right">
            <el-button type="primary" @click="Cerrar()">Cerrar</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-main>
  </el-container>
</template>
<script>
import { liquidacionDePrueba } from '@/api/credito'

export default {
  name: 'LiquidacionDePruebaComponent',
  props: {
    colocacion: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      form: {
        cuotas: 1,
        fecha_corte: new Date()
      },
      capital: 0,
      causado: 0,
      servicio: 0,
      anticipado: 0,
      devuelto: 0,
      mora: 0,
      otro: 0,
      costa: 0,
      total: 0,
      tableData: []
    }
  },
  beforeMount () {
    console.log("Colocacion: " + JSON.stringify(this.colocacion))
  },
  methods: {
    Cerrar() {
      this.$emit('cerrarLiquidacionDePruebaEvent')
    },
    liquidar() {
      this.tableData = []
      this.capital = 0
      this.causado = 0
      this.servicio = 0
      this.anticipado = 0
      this.devuelto = 0
      this.mora = 0
      this.otro = 0
      this.costa = 0
      this.total = 0
      liquidacionDePrueba(this.colocacion.id_colocacion, this.form.cuotas, this.form.fecha_corte.getTime()).then(response => {
        var items = response.data.items
        items.sort((a, b) => a.codigoPuc < b.codigoPuc)
        items.forEach(i => {
          if (i.esCapital) {
            this.capital = this.capital + i.credito - i.debito
          } else if (i.esCausado) {
            this.causado = this.causado + i.credito - i.debito
          } else if (i.esCorriente) {
            this.servicio = this.servicio + i.credito - i.debito
          } else if (i.esVencido) {
            this.mora = this.mora + i.credito - i.debito
          } else if (i.esDevuelto) {
            this.devuelto = this.devuelto + i.debito - i.credito
          } else if (i.esAnticipado) {
            this.anticipado = this.anticipado + i.credito - i.debito
          } else if (i.esOtros) {
            this.otro = this.otro + i.credito - i.debito
          } else if (i.esCajaBanco) {
            this.total = this.total + i.debito - i.credito
          }
        })
        this.tableData = items
      })
    }
  }
}
</script>
<style>
.td {
  padding: 0px 0px;
}
.el-dialog__body {
  padding: 0px 20px !important;
}
.el-main {
  padding: 0px 20px !important;
}
</style>
