<template>
  <el-container>
    <el-main>
      <el-row>
        <el-col :span="24">
          <el-table :data="datos" :show-summary="true" :summary-method="totales" :row-key="row => row.id_cbte_colocacion" :expand-row-keys="expandRowKeys" sum-text="TOT" stripe style="width: 100%; font-size: 12px;" max-height="300" @expand-change="buscarDetalle">
            <el-table-column type="expand">
              <template slot-scope="detalle">
                <el-table :data="detalle.row.datos" style="width: 100%; font-size: 11px;">
                  <el-table-column label="Código" width="150">
                    <template slot-scope="scope">
                      <span style="margin-left: 10px">{{ scope.row.codigo_puc }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="Cuenta" width="250">
                    <template slot-scope="scope">
                      <span style="margin-left: 10px">{{ scope.row.codigo_nombre | fm_truncate(30) }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="F. Inicial" width="90">
                    <template slot-scope="scope">
                      <span style="margin-left: 10px">{{ scope.row.fecha_inicial | moment("YYYY-MM-DD") }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="F. Final" width="90">
                    <template slot-scope="scope">
                      <span style="margin-left: 10px">{{ scope.row.fecha_final | moment("YYYY-MM-DD") }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="Días" width="80">
                    <template slot-scope="scope">
                      <span style="margin-left: 10px">{{ scope.row.dias_aplicados }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="%Tasa" width="80">
                    <template slot-scope="scope">
                      <span style="margin-left: 10px">{{ scope.row.tasa_liquidacion }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="Débito" width="150" align="right">
                    <template slot-scope="scope">
                      <span style="margin-left: 10px;">{{ scope.row.valor_debito | currency }}</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="Crédito" width="150" align="right">
                    <template slot-scope="scope">
                      <span style="margin-left: 10px">{{ scope.row.valor_credito | currency }}</span>
                    </template>
                  </el-table-column>
                </el-table>
              </template>
            </el-table-column>
            <el-table-column sortable label="Fecha" prop="fecha_extracto" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_extracto | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Nota" prop="id_cbte_colocacion" width="80">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.id_cbte_colocacion }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Cuota" prop="cuota_extracto" align="right" width="80">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.cuota_extracto }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Capital" prop="abono_capital" align="right" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.abono_capital | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="%Cobro" prop="tasa_interes_liquidacion" align="right" width="80">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.tasa_interes_liquidacion }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Interés" prop="abono_interes" align="right" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.abono_cxc + scope.row.abono_anticipado + scope.row.abono_servicios | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Saldo" prop="saldo_anterior_extracto" align="right" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.saldo_anterior_extracto - scope.row.abono_capital | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="F.Vencimiento" prop="interes_pago_hasta" align="right" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.interes_pago_hasta | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="D. Mora" prop="dias_mora" align="right" width="80">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.adicional.dias_mora }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Cap. Hasta" prop="capital_pago_hasta" align="right" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.capital_pago_hasta | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Int. Hasta" prop="interes_pago_hasta" align="right" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.interes_pago_hasta | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-button type="primary" @click="Cerrar()">Cerrar</el-button>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>

import { obtenerExtractoColocacionDetallado } from '@/api/extractocolocacion'

export default {
  name: 'ExtractoColocacionComponent',
  props: {
    colocacion: {
      type: String,
      required: true
    },
    datos: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      expandRowKeys: []
    }
  },
  methods: {
    Cerrar() {
      this.$emit('cerrarExtractoEvent')
    },
    buscarDetalle(row, expanded) {
      if (row && row.datos === undefined) {
        const fecha = Date.parse(row.fecha_extracto)
        obtenerExtractoColocacionDetallado(row.id_colocacion, row.id_cbte_colocacion, fecha).then(response => {
          row.datos = response.data
          const id = row.id_cbte_colocacion
          var lastId = -1
          if (this.expandRowKeys.length > 0) {
            lastId = this.expandRowKeys[0]
          }
          this.expandRowKeys = id === lastId ? [] : [id]
        })
      }
    },
    getRowKey(row) {
      return row.id_cbte_colocacion
    },
    totales(param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 1) {
          sums[index] = 'Total Capital'
          return
        } else if (index !== 4) {
          return
        }
        const values = data.map(item => Number(item[column.property]))
        if (!values.every(value => isNaN(value))) {
          const total = values.reduce((prev, curr) => {
            const value = Number(curr)
            if (!isNaN(value)) {
              return prev + curr
            } else {
              return prev
            }
          }, 0)
          sums[index] = '$ ' + total.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.')
        } else {
          sums[index] = ''
        }
      })
      return sums
    }
  }
}
</script>
