<template>
  <el-container style="height: 600px; overflow: scroll;">
    <el-header>
      <span>Nueva Solicitud</span>
    </el-header>
    <el-main>
      <el-form
        ref="form"
        :model="form"
        label-position="top">
        <el-collapse v-model="activeNames">
          <el-collapse-item name="1" title="Información del Solicitante">
            <el-row :gutter="4">
              <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                <el-form-item prop="id_identificacion" label="Tipo de Documento">
                  <el-select :disabled="es_bloqueo" v-model="form.id_identificacion" filterable>
                    <el-option v-for="t in tipo_documento" :key="t.id_identificacion" :label="t.descripcion_identificacion" :value="t.id_identificacion" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="5" :lg="5" :xl="5">
                <el-form-item prop="id_persona" label="Número de Documento">
                  <el-input :disabled="es_bloqueo" v-model="form.id_persona" @blur="buscarPersona" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                <el-form-item prop="id_persona" label="Nombre Completo">
                  <el-input v-model="nombre" readonly />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="2" :lg="2" :xl="2">
                <el-form-item>
                  <el-button :disabled="form.id_identificacion == null || form.id_persona == null" type="success" icon="el-icon-edit" circle title="Actualizar Información de la Persona" @click="actualizarPersona(form.id_identificacion, form.id_persona)"/>
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
          <el-collapse-item v-if="solicitante_existe" name="2" title="INFORMACIÓN DE LA SOLICITUD">
            <el-row>
              <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
                <el-form-item label="Fecha Recepcion">
                  <el-date-picker v-model="form.fecha_recepcion" readonly />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
                <el-form-item label="Valor Solcitado">
                  <el-input
                    v-currency= "{
                      currency: 'COP',
                      locale: es,
                      distractionFree: true,
                      min: null,
                      max: null,
                      validateOnInput: false
                    }"
                    v-model="form.valor_solicitado" />
                </el-form-item>
              </el-col>
              <el-col :xs="24" :sm="24" :md="8" :lg="8" :xl="8">
                <el-form-item label="Tipo de Cuota">
                  <el-select v-model="form.tipo_cuota" filterable>
                    <el-option v-for="t in tipos_cuota" :key="t.id" :label="t.descripcion" :value="t.id" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
        </el-collapse>
      </el-form>
      <el-dialog
        :visible.sync="dialogPersonaVisible"
        class="pdialog">
        <PersonaComponent :id_identificacion="id_identificacion" :id_persona="id_persona" />
        <el-button type="primary" @click="dialogPersonaVisible = false" >Cerrar</el-button>
      </el-dialog>
    </el-main>
  </el-container>
</template>
<script>
import PersonaComponent from '@/components/Persona'
import { obtenerListaTipoIdentificacion } from '@/api/tipos'
import { obtenerPersona } from '@/api/persona'

export default {
  components: {
    PersonaComponent
  },
  data() {
    return {
      activeNames: ['1', '2', '3'],
      dialogPersonaVisible: false,
      solicitanteNoValido: true,
      tipo_documento: [],
      tipos_cuota: [],
      id_identificacion: null,
      id_persona: null,
      nombre: null,
      es_bloqueo: false,
      codeudorNoValido: true,
      form: {
        id_asesor: null,
        id_solicitud: null,
        id_identificacion: null,
        id_persona: null,
        valor_solicitado: null,
        plazo: null,
        amortizacion: null,
        garantia: null,
        tipo_cuota: null,
        estado: null,
        linea: null,
        fecha_recepcion: new Date(),
        oficina: null,
        destino: null,
        fecha_concepto: null,
        id_empleado: null,
        ente_aprobador: null,
        numero_acta: null,
        tasa_interes: null,
        plazo_aprobado: null,
        descripcion_garantia: null,
        numero_codeudores: null,
        respaldo: null,
        valor_aprobado: null,
        id_analisis: null,
        ingresos: null,
        disponible: null,
        deducciones: null,
        valor_cuota: null,
        disponibilidad: null,
        solv_economica: null,
        exp_creditos: null,
        inversion: null,
        clasificacion: null,
        es_desembolso_parcial: null,
        pago_interes: null,
        id_copia: null,
        observacion: null,
        s_vida: null,
        s_exequial: null,
        es_fundacion: null,
        numero_riesgo: null,
        fecha_analisis: null,
        codeudores: []
      }
    }
  },
  beforeMount() {
    obtenerListaTipoIdentificacion().then(response => {
      this.tipo_documento = response.data
    }).catch(error => {
      console.log('Error consultando tipo identificación:' + error)
    })
  },
  methods: {
    buscarPersona() {
      const loading = this.$loading({
        lock: true,
        text: 'Cargando...',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      obtenerPersona(this.form.id_identificacion, this.form.id_persona).then(response => {
        loading.close()
        if (response.status === 200) {
          this.id_identificacion = response.data.a.id_identificacion
          this.id_persona = response.data.a.id_persona
          this.nombre = response.data.a.nombre + ' ' + response.data.a.primer_apellido + ' ' + response.data.a.segundo_apellido
          this.solicitante_existe = true
        }
      }).catch(() => {
        this.nombre = null
        this.solicitante_existe = false
        loading.close()
        this.$alert('Persona No Existe', 'Buscando Persona')
        this.actualizarPersona(this.form.id_identificacion, this.form.id_persona)
      })
    },
    actualizarPersona(id_identificacion, id_persona) {
      this.id_identificacion = id_identificacion
      this.id_persona = id_persona
      this.dialogPersonaVisible = true
    },
    borrarCodeudor(didx) {
      this.$confirm('Seguro de Borrar al Codeudor. Continuar?', 'Advertencia', {
        confirmButtonText: 'Sí',
        cancelButtonText: 'No',
        type: 'warning'
      }).then(() => {
        this.form.codeudores.splice(didx, 1)
        var i = 1
        this.form.codeudores.forEach(c => {
          c.consecutivo = i
          i++
        })
        this.$message({
          type: 'success',
          message: 'Borrado Finalizado'
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Borrado Cancelado'
        })
      })
    },
    onAddCodeudor() {
      const codeudor = {
        consecutivo: null,
        id_identificacion: null,
        id_persona: null,
        nombre: null,
        es_codeudor: null,
        es_conyuge: null
      }
      const csc = this.form.codeudores.length + 1
      codeudor.consecutivo = parseInt(csc)
      this.form.codeudores.push(codeudor)
    },
    onGuardarSolicitud() {
      this.dialogPersonaVisible = false
      this.form.retefuente = this.form.retefuente ? 1 : 0
      this.form.cabezafamilia = this.form.cabezafamilia ? 1 : 0
      guardarSolicitud(this.form).then(response => {
        if (response.status === 200) {
          this.$message({
            message: 'Información guardada correctamente!',
            type: 'success'
          })
        } else {
          this.$message({
            message: 'No se pudo guardar la información, por favor verifique los datos.',
            type: 'warning'
          })
        }
      }).catch(error => {
        this.$message.error('Se presentó error guardando persona: ' + error)
      })
    }
  }
}
</script>
<style lang="scss" scoped>
  .pdialog {
    .el-dialog {
      width: 80% !important;
    }
  }
</style>
