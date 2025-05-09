console.log("this is script file ");

const search = () => {
	// console.log("Searching...");

	let query = $("#search-input").val();

	if (query == '') {
		$(".search-result").hide();
	}
	else {
		// search 
		console.log(query);

		// sending request to server

		let url = `/search/${query}`;

		fetch(url).then((response) => {
			return response.json();
		})
			.then((data) => {
				// data...........
				console.log(data);
				
				let text = `<div class='list-group'>`
				
				data.forEach((contact) => {
					text+=`<a href='/user/contact/${contact.cId}' class='list-group-item list-group-action' > ${contact.name} </a>`
				})
				
				text+=`</div>`;
				
				$(".search-result").html(text);
				$(".search-result").show();
			});


		
	}
}