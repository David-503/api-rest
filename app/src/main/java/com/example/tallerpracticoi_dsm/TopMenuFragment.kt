package com.example.tallerpracticoi_dsm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
enum class originType {
    BASIC,
    GOOGLE,
    GITHUB
}
class TopMenuFragment : Fragment() {
    private var origin: originType? = null
    private var email: String? = null
    lateinit var logoutButton: Button;
    lateinit var txtEmail: TextView;
    lateinit var profilePic : ImageView;
    // Declaracion de variables
    private lateinit var bottomNavigationBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            email = it.getString(EMAIL)
            origin = enumValueOf<originType>(it.getString(ORIGIN).toString())
            Toast.makeText(context,"Origin Type " + origin,Toast.LENGTH_SHORT).show()

        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_menu, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val preps = activity?.getSharedPreferences(getString(R.string.preps_file),Context.MODE_PRIVATE)
        val emailProps: String? = preps?.getString("email",null)
        val providerProps: String? = preps?.getString("provider",null)
        if(emailProps != null && providerProps != null){
            email = emailProps;
            origin = enumValueOf<originType>(providerProps)
        }
        super.onViewCreated(view, savedInstanceState)
        logoutButton =requireView().findViewById(R.id.btn_logout)
        logoutButton!!.setOnClickListener { logOut() }
          txtEmail = requireView().findViewById(R.id.txtEmail);
         txtEmail.text=email;
        profilePic = requireView().findViewById(R.id.imageProfile);
        when (origin) {
            originType.BASIC -> profilePic.setImageResource(R.drawable.email32_ligth)
            originType.GITHUB -> profilePic.setImageResource(R.drawable.github32_ligth)
            originType.GOOGLE -> profilePic.setImageResource(com.google.firebase.database.ktx.R.drawable.common_google_signin_btn_icon_light)
            else -> { // Note the block
                profilePic.setImageResource(R.drawable.user32)
            }
        }

    }
     fun logOut(){
        FirebaseAuth.getInstance().signOut().also {
            Toast.makeText(context,"Logout",Toast.LENGTH_SHORT).show()
            val intent = Intent(context, LoginActivity::class.java)
            val preps = context?.getSharedPreferences(getString(R.string.preps_file), Context.MODE_PRIVATE)?.edit()
            preps?.clear()
            preps?.apply()
            startActivity(intent)
        }


    }
    companion object {
        private val ORIGIN = "origin"
        private val EMAIL = "email"

        fun newInstance(email:String,origin: String) =
            TopMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ORIGIN, origin)
                    putString(EMAIL, email)

                }
            }
    }
}