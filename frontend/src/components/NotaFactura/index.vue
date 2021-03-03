<template>
  <el-container>
    <el-header>
      <h1>Nota Factura Electrónica</h1>
    </el-header>
    <el-main>
      <el-form :inline="true" :model="factura">
        <el-form-item label="Factura Número">
          <el-input v-model="factura.fact_numero" @change="factura.fact_numero=parseInt($event)" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="getFacturaPorNumero" />
        </el-form-item>
      </el-form>
      <el-row :gutter="4">
        <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
          <b>Fecha</b>
        </el-col>
        <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
          <span>{{ factura.fact_fecha | moment('YYYY-MM-DD') }}</span>
        </el-col>
        <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
          <b>Nombre</b>
        </el-col>
        <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
          <span>{{ fullName }}</span>
        </el-col>        
      </el-row>
      <el-row>
        <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
          <b>Documento</b>
        </el-col>
        <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
          <span>{{ identity }}</span>
        </el-col>
        <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
          <b>Descripción</b>
        </el-col>
        <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
          <span>{{ factura.fact_descripcion }}</span>
        </el-col>
      </el-row>
      <el-row>
        <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
          <b>Tipo Comprobante</b>
        </el-col>
        <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
          <span>{{ tipocomprobante(factura.tipo_comprobante) }}</span>
        </el-col>
        <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
          <b>Número Comprobante</b>
        </el-col>
        <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
          <span>{{ factura.id_comprobante }}</span>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-table
            :data="factura.items"
            :summary-method="getSummaries"
            show-summary
            stripe
            style="font-size: 14px;"
            size="mini"
            max-height="250px"
          >
            <el-table-column
              label="Item"
              type="index"
              width="50">
            </el-table-column>
            <el-table-column
              label="Descripción"
              width="250">
              <template slot-scope="item">
                <span>{{ item.row.fait_detalle}}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="Vr. Unitario"
              align="right"
              width="110">
              <template slot-scope="item">
                <span>{{ item.row.fait_valorunitario | currency_2('$')}}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="Qty"
              align="center">
              <template slot-scope="item">
                <span>{{ item.row.fait_cantidad }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="Iva%"
              align="center">
              <template slot-scope="item">
                <span>{{ item.row.fait_tasaiva }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="Iva"
              align="right"
              width="110">
              <template slot-scope="item">
                <span>{{ item.row.fait_valoriva | currency_2('$')}}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="SubTotal"
              align="right"
              width="110"
              prop="fait_total">
              <template slot-scope="item">
                <span>{{ item.row.fait_total | currency_2('$')}}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <b>Factura CUFE:</b>{{ this.nota.fact_cufe }}
          <el-button type="success" size="mini" v-if="this.nota.fact_cufe" @click="facturaVisible=true">Ver Factura</el-button>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
      <el-form :disabled="notfound" size="mini" style="height: 90vh; overflow-y: auto;">
      <el-row>
        <el-col>
          <el-form-item label="Seleccione Débito o Crédito">
            <el-radio v-model="nota.fact_nota_tipo" label="D">Nota Débito</el-radio>
            <el-radio v-model="nota.fact_nota_tipo" label="C">Nota Crédito</el-radio>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row style="overflow-y: auto;">
        <el-col>
          <el-form-item label="Descripción de la Naturaleza de la Corrección">
            <el-input   
              type="textarea"
              :rows="3"
              placeholder="Digite aquí la descripción" 
              v-model="nota.fact_nota_descripcion"
              maxlength="250"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="4">
        <el-col :xs="24" :sm="24" :md="6" :lg="6" :xl="6">
          <el-form-item label="Detalle">
            <el-input v-model="item.fanoit_detalle" />
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
          <el-form-item label="Vr. Unitario">
            <currency-input currency="COP" locale="es" :value-as-integer="false" v-model="item.fanoit_valorunitario" style="width: 95%;" @blur="calcularItem()"/>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
          <el-form-item label="Qty">
            <el-input v-model="item.fanoit_cantidad" @blur="calcularItem()"/>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="24" :md="3" :lg="3" :xl="3">
          <el-form-item label="Iva%">
            <el-input type="number" v-model="item.fanoit_tasaiva" @blur="calcularItem()"/>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
          <el-form-item label="Vr. Iva">
            <currency-input readonly currency="COP" locale="es" :value-as-integer="false" v-model="item.fanoit_valoriva" style="width: 95%;" @blur="calcularItem()"/>
          </el-form-item>
        </el-col>
        <el-col :xs="24" :sm="24" :md="4" :lg="4" :xl="4">
          <el-form-item label="Subtotal">
            <currency-input currency="COP" locale="es" :value-as-integer="false" v-model="item.fanoit_total" style="width: 95%;" @blur="calcularItem()"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-button type="primary" circle size="mini" icon="el-icon-plus" title="Agregar Item" @click="nuevoItem" />
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-table
            :data="nota.items"
            :summary-method="getSummaries"
            show-summary
            stripe
            style="font-size: 14px;"
            size="mini"
            max-height="250px"
            :selection-change="handleItemSelectionChange"
          >
            <el-table-column
              label="Item"
              type="index"
              width="50">
            </el-table-column>
            <el-table-column
              label="Detalle"
              width="250">
              <template slot-scope="item">
                <span>{{ item.row.fanoit_detalle}}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="Vr. Unitario"
              align="right"
              width="110">
              <template slot-scope="item">
                <span>{{ item.row.fanoit_valorunitario | currency_2('$')}}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="Qty"
              align="center">
              <template slot-scope="item">
                <span>{{ item.row.fanoit_cantidad }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="Iva%"
              align="center">
              <template slot-scope="item">
                <span>{{ item.row.fanoit_tasaiva }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="Iva"
              align="right"
              width="110">
              <template slot-scope="item">
                <span>{{ item.row.fanoit_valoriva | currency_2('$')}}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="SubTotal"
              align="right"
              width="110"
              prop="fanoit_total">
              <template slot-scope="item">
                <span>{{ item.row.fanoit_total | currency_2('$')}}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
      <el-row>
        <el-col>
          <el-button type="primary" icon="el-icon-files" @click="crearNota">Crear y Enviar Nota</el-button>
        </el-col>
      </el-row>
      </el-form>
        </el-col>
      </el-row>
    </el-main>
    <el-dialog
      :visible.sync="facturaVisible"
      :append-to-body="true"
      width="80%"
      height="80%"
    >
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" size="mini" @click="facturaVisible = false">Cerrar</el-button>
      </div>
      <iframe :src="factura_pdf" width="100%" height="600px"/>
    </el-dialog>
  </el-container>
</template>
<script>
import { currency } from '@/utils/math'
import { getFactura, getFacturaProveedor, crearNotaDebito, crearNotaCredito, enviarNotaDebito, enviarNotaCredito } from '@/api/factura'
import { obtenerTiposComprobante } from '@/api/contabilidad'
import { obtenerListaTipoIdentificacion } from '@/api/tipos'

export default {
  props: {
    tipo: {
      type: String,
      required: false,
      default: 'D'
    }
  },
  data () {
    return {
      factura: {
        fact_numero: null,
        fact_fecha: null,
        fact_descripcion: null,
        id_identificacion: null,
        id_persona: null,
        primer_apellido: null,
        segundo_apellido: null,
        nombre: null,
        items: []
      },
      factura_pdf: null,
      facturaVisible: false,
      tipo_comprobante: [],
      tipo_identificacion: [],
      nota: {
        fact_nota_tipo: null,
        fact_nota_descripcion: null,
        fact_cufe: null,
        fact_numero: null,
        items: []
      },
      item: {
        fanoit_detalle: null,
        fanoit_valorunitario: null,
        fanoit_cantidad: 1,
        fanoit_tasaiva: 0,
        fanoit_valoriva: null,
        fanoit_total: null
      },
      itemIndex: null,
      notfound: true
    }
  },
  computed: {
    fullName () {
      var full = ''
      if (this.factura.nombre != null) {
        full += this.factura.nombre + ' '
      }
      if (this.factura.primer_apellido != null) {
        full += this.factura.primer_apellido + ' '
      }
      if (this.factura.segundo_apellido != null) {
        full += this.factura.segundo_apellido
      }
      return full
    },
    identity () {
      var id = ''
      if (this.factura.id_identificacion != null) {
        id += this.tipoidentificacion(this.factura.id_identificacion) + ' '
      }
      if (this.factura.id_persona != null) {
        id += this.factura.id_persona
      }
      return id
    }
  },
  mounted () {
    obtenerTiposComprobante().then(response => {
      this.tipo_comprobante = response.data
      obtenerListaTipoIdentificacion().then(response => {
        this.tipo_identificacion = response.data
        this.nota.fact_nota_tipo = this.tipo
      })
    })
  },
  methods: {
    crearNota () {
      this.$confirm('Seguro de Crear y Enviar la Nueva Nota?', 'Atención', {
        confirmButtonText: 'Si',
        cancelButtonText: 'No',
        type: 'warning'
      }).then(() => {
        this.nota.fact_numero = this.factura.fact_numero
        if (this.nota.fact_nota_tipo === 'D') {
          crearNotaDebito(this.nota).then(response => {
            if (response.status === 200) {
              if (response.data > 0) {
                enviarNotaDebito(response.data)
              } else {
                this.$alert('No se pudo crear la Nota', 'Error', {
                  confirmButtonText: 'Ok',
                })
              }
            }
          })
        } else if (this.nota.fact_nota_tipo === 'C')
        {
          crearNotaCredito(this.nota).then(response =>{
            if (response.status === 200) {
              if (response.data > 0) {
                enviarNotaCredito(response.data)
              } else {
                this.$alert('No se pudo crear la Nota', 'Error', {
                  confirmButtonText: 'Ok',
                })
              }
            }
          })
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Creación Cancelada'
        })
      })
    },
    getSummaries(param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 1) {
          sums[index] = 'Total'
          return;
        }
        const values = data.map(item => Number(item[column.property]));
        if (!values.every(value => isNaN(value))) {
          sums[index] = '$' + currency(values.reduce((prev, curr) => {
            const value = Number(curr)
            if (!isNaN(value)) {
              return prev + curr
            } else {
              return prev
            }
          }, 0),0)
        } else {
          sums[index] = '-'
        }
      })
      return sums
    },    
    limpiarFactura () {
      this.factura = {
        fact_numero: null,
        fact_fecha: null,
        fact_descripcion: null,
        id_identificacion: null,
        id_persona: null,
        primer_apellido: null,
        segundo_apellido: null,
        nombre: null,
        items: []
      }
    },
    tipocomprobante(id) {
      if (id) {
        let tipo = this.tipo_comprobante.find(t => parseInt(t.id) === parseInt(id))
        if (tipo) {
          return tipo.descripcion
        } else {
          return 'No existe el Id'
        }
      } else {
        return ''
      }
    },
    tipoidentificacion(id) {
      if (id) {
        let tipo = this.tipo_identificacion.find(t => t.id_identificacion === id)
        if (tipo) {
          return tipo.descripcion_identificacion
        } else {
          return ''
        }
      } else {
        return ''
      }
    },  
    getFacturaPorNumero () {
      getFactura(this.factura.fact_numero).then(response => {
        this.factura = response.data
        this.notfound = false
        this.getCufe()
      }).catch((e) => {
        this.$message({
          message: 'Atención, Factura no Existe.' + e,
          type: 'warning'
        })
        this.limpiarFactura()
      })
    },
    getCufe () {
      const periodo = this.$moment(this.factura.fact_fecha).format('YYYYMM')
      getFacturaProveedor(this.factura.fact_numero, periodo).then(response =>{
        this.nota.fact_cufe = response.data.GetTransaccionbyIdentificacionResult.CodigoTransaccion
        if (this.nota.fact_cufe) {
          this.factura_pdf = 'data:application/pdf;base64,' + response.data.GetTransaccionbyIdentificacionResult.PDFBase64
        }
      })
    },
    calcularItem () {
      if (this.item.fanoit_valorunitario) {
        this.item.fanoit_valoriva = Math.round(this.item.fanoit_valorunitario * this.item.fanoit_cantidad * this.item.fanoit_tasaiva / 100)
        this.item.fanoit_total = (this.item.fanoit_valorunitario * this.item.fanoit_cantidad) + this.item.fanoit_valoriva
      }
    },
    nuevoItem () {
      this.nota.items.push(this.item)
      this.item = {
        fanoit_detalle: null,
        fanoit_valorunitario: null,
        fanoit_cantidad: 1,
        fanoit_tasaiva: 0,
        fanoit_valoriva: null,
        fanoit_total: null
      }
    },
    handleItemSelectionChange (item) {
      console.log('item:' + item)
      this.item = item
    }
  }
}
</script>