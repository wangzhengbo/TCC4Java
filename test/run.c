int main(int argc, char *argv[]) {
	int i = 0, sum = 0;
	for(i = 1; i < argc; i++) {
		sum += atoi(argv[i]);
	}
	
	return sum;
}
