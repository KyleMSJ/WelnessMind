package ifsp.project.welnessmind.presentation.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ifsp.project.welnessmind.data.db.entity.ProfessionalEntity
import ifsp.project.welnessmind.databinding.ProfessionalItemBinding

class ProfessionalListAdapter(
    private val professionals: List<ProfessionalEntity>
): RecyclerView.Adapter<ProfessionalListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = ProfessionalItemBinding.inflate(view, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(professionals[position])
    }

    inner class ViewHolder(
        private val binding: ProfessionalItemBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bindView(professional: ProfessionalEntity) =  with(binding) {
            textProName.text = professional.name
            textProEspecialidade.text = professional.especialidade
            textProEmail.text = professional.email
        }
    }

    override fun getItemCount(): Int = professionals.size

}