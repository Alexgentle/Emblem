using UnityEngine;
using System.Collections;

public class EPlayer : MonoBehaviour
{
	public Vector3 position;
	public Vector3 velocity;
	public string id;
	
	void FixedUpdate()
	{
		transform.Translate(velocity);
		//position = transform.position;
	}
	
	//public static bool operator == (EPlayer e, EPlayer p)
    //{
	//	return e.id == p.id;
	//}
	
	//public static bool operator != (EPlayer e, EPlayer p)
    //{
	//	return e.id != p.id;
	//}
}

